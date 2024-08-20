import datetime
import random
import logging
import string
import json
from contextlib import contextmanager
from typing import List

from sqlalchemy import create_engine
from sqlalchemy import MetaData
from sqlalchemy import select
from sqlalchemy import create_engine, Table, Column, Integer, String, Float, JSON, TIMESTAMP, MetaData
from sqlalchemy.sql import func
from sqlalchemy import text
from sqlalchemy.orm import scoped_session
from sqlalchemy.orm import sessionmaker
from sqlalchemy.exc import SQLAlchemyError
from sqlalchemy.dialects.postgresql import JSONB

from app.linkage.utils import load_mpi_env_vars_os

def generate_custom_id():
        """
        Generates a custom ID with 3 random alphabets followed by 6 random digits.
        """
        letters = ''.join(random.choices(string.ascii_uppercase, k=3))
        numbers = ''.join(random.choices(string.digits, k=3))
        return f"{letters}{numbers}"

class DataAccessLayer(object):
    """
    Base class for Database API objects - manages transactions,
    sessions and holds a reference to the engine.
    Acts as a simple session context manager and creates a
    uniform API for querying using the ORM
    This class could be thought of as a singleton factory.
    Applications should only ever use one instance per database.
    Example:
        dal = DataAccessLayer()
        dal.connect(engine_url=..., engine_echo=False)
    """

    def __init__(self) -> None:
        self.engine = None
        self.Meta = MetaData()
        self.PATIENT_TABLE = None
        self.PERSON_TABLE = None
        self.NAME_TABLE = None
        self.GIVEN_NAME_TABLE = None
        self.ID_TABLE = None
        self.PHONE_TABLE = None
        self.ADDRESS_TABLE = None
        self.EXTERNAL_PERSON_TABLE = None
        self.EXTERNAL_SOURCE_TABLE = None
        self.configurations = None
        self.pass_configurations = None
        self.TABLE_LIST = []
        self.metadata = MetaData()
        

    def get_connection(
        self,
        engine_url: str,
        engine_echo: bool = False,
        pool_size: int = 5,
        max_overflow: int = 10,
    ) -> None:
        """
        Establish a connection to the database

        this method initiates a connection to the database specified
        by the parameters defined in environment variables. Builds
        engine and Session class for app layer

        :param engine_url: The URL of the database engine
        :param engine_echo: If True, print SQL statements to stdout
        :param pool_size: The number of connections to keep open in the connection pool
        :param max_overflow: The number of connections to allow in the connection pool
          “overflow”
        :return: None
        """
        try:
            self.engine = create_engine(
                engine_url,
                client_encoding="utf8",
                echo=engine_echo,
                pool_size=pool_size,
                max_overflow=max_overflow,
            )
            self.connection = self.engine.connect()  # Establish connection
            self.metadata.bind = self.engine
            print("Database connection established.")
        except Exception as e:
            print(f"An error occurred while establishing a connection: {e}")

    def initialize_schema(self) -> None:
        """
        Initialize the database schema

        This method initializes all the MPI Database tables using SQLAlchemy's
        Table object

        :return: None
        """

        self.PATIENT_TABLE = Table("patient", self.Meta, autoload_with=self.engine)
        self.PERSON_TABLE = Table("person", self.Meta, autoload_with=self.engine)
        self.NAME_TABLE = Table("name", self.Meta, autoload_with=self.engine)
        self.GIVEN_NAME_TABLE = Table(
            "given_name", self.Meta, autoload_with=self.engine
        )
        self.ID_TABLE = Table("identifier", self.Meta, autoload_with=self.engine)
        self.PHONE_TABLE = Table("phone_number", self.Meta, autoload_with=self.engine)
        self.ADDRESS_TABLE = Table("address", self.Meta, autoload_with=self.engine)
        self.EXTERNAL_PERSON_TABLE = Table(
            "external_person", self.Meta, autoload_with=self.engine
        )
        self.EXTERNAL_SOURCE_TABLE = Table(
            "external_source", self.Meta, autoload_with=self.engine
        )
        self.configurations = Table("configurations", self.Meta, autoload_with=self.engine)

        # order of the list determines the order of
        # inserts due to FK constraints
        self.TABLE_LIST = []
        self.TABLE_LIST.append(self.PERSON_TABLE)
        self.TABLE_LIST.append(self.EXTERNAL_SOURCE_TABLE)
        self.TABLE_LIST.append(self.EXTERNAL_PERSON_TABLE)
        self.TABLE_LIST.append(self.PATIENT_TABLE)
        self.TABLE_LIST.append(self.NAME_TABLE)
        self.TABLE_LIST.append(self.GIVEN_NAME_TABLE)
        self.TABLE_LIST.append(self.ID_TABLE)
        self.TABLE_LIST.append(self.PHONE_TABLE)
        self.TABLE_LIST.append(self.ADDRESS_TABLE)
        self.TABLE_LIST.append(self.configurations)

    def initialize_config_schema(self):
        # Define tables and their columns
        Table(
            'configurations', self.metadata,
            Column('id', Integer, primary_key=True, autoincrement=True),
            Column('name', String(255), nullable=False),
            Column('belongingness_ratio', Float, nullable=False),
            Column('thresholds', JSON, nullable=False),
            Column('created_at', TIMESTAMP, server_default=func.now()),
            Column('updated_at', TIMESTAMP, server_default=func.now(), onupdate=func.now())
        )
        # Create all tables in the metadata
        try:
            self.metadata.create_all(self.engine)
            print("Schema initialized successfully.")
        except Exception as e:
            print(f"An error occurred while initializing schema: {e}")

    def initialize_pass_config_schema(self):
        # Define the pass_configurations table and its columns
        Table(
            'pass_configurations', self.metadata,
            Column('id', Integer, primary_key=True, autoincrement=True),
            Column('pass_name', String(255), nullable=False),
            Column('description', String(255), nullable=False),
            Column('index', Integer, nullable=False),
            Column('lowerbound', Float, nullable=False),
            Column('upperbound', Float, nullable=False),
            Column('matching_criteria', JSON, nullable=False),
            Column('blocks', JSON, nullable=False),
            Column('status', String(255), nullable=False),  # This will store the active/inactive status
            Column('created_at', TIMESTAMP, server_default=func.now()),
            Column('updated_at', TIMESTAMP, server_default=func.now(), onupdate=func.now())
        )
        # Create all tables in the metadata
        try:
            self.metadata.create_all(self.engine)
            print("Pass configurations schema initialized successfully.")
        except Exception as e:
            print(f"An error occurred while initializing pass configurations schema: {e}")

    def get_configurations(self) -> List[dict]:
        """
        Retrieve all configurations from the database.
        """
        try:
            if self.engine is None:
                raise ValueError("Database engine is not initialized.")
            config_table = Table("configurations", self.Meta, autoload_with=self.engine)
            with self.get_session() as session:
                query = session.query(config_table).all()
                return [dict(row) for row in query]
        except SQLAlchemyError as e:
            logging.error(f"An error occurred while retrieving configurations: {e}")
            return []

    @contextmanager
    def transaction(self) -> None:
        """
        Execute a database transaction

        this method safely wraps a session object in a transactional scope
        used for basic create, select, update and delete procedures

        :yield: SQLAlchemy session object
        :raises ValueError: if an error occurs during the transaction
        """
        session = self.get_session()

        try:
            yield session
            session.commit()

        except Exception as error:
            session.rollback()
            raise ValueError(f"{error}")

        finally:
            session.close()

    def bulk_insert_list(
        self, table: Table, records: list[dict], return_primary_keys: bool = True
    ) -> list:
        """
        Perform a bulk insert operation on a table.  A list of records
        as dictionaries are inserted into the specified table.  A list
        of primary keys from the bulk insert can be returned if return_pks
        is set to True.

        :param table_object: the SQLAlchemy table object to insert into
        :param records: a list of records as a dictionaries
        :param return_primary_key: boolean indicating if you want the inserted
            primary keys for the table returned or not, defaults to False
        :return: a list of primary keys or an empty list
        """
        new_primary_keys = []
        if len(records) > 0 and table is not None:
            logging.info(
                f"Getting primary_key_column at:{datetime.datetime.now().strftime('%m-%d-%yT%H:%M:%S.%f')}"  # noqa
            )
            primary_key_column = table.primary_key.c[0]
            with self.transaction() as session:
                logging.info(
                    f"Starting session at: {datetime.datetime.now().strftime('%m-%d-%yT%H:%M:%S.%f')}"  # noqa
                )
                n_records = 0
                for record in records:
                    n_records += 1
                    if return_primary_keys:
                        logging.info("Returned primary keys")
                        statement = (
                            table.insert().values(record).returning(primary_key_column)
                        )
                        logging.info(
                            f"""Starting statement execution getting
                              new_primary_key for record #{n_records}at:
                                {datetime.datetime.now().strftime('%m-%d-%yT%H:%M:%S.%f')}"""  # noqa
                        )
                        new_primary_key = session.execute(statement)
                        # TODO: I don't like this, but seems to
                        # be one of the only ways to get this to work
                        #  I have tried using the column name from the
                        # PK defined in the table and that doesn't work
                        logging.info(
                            f""" Done with statement execution getting new_primary_key
                              for record #{n_records} at: {datetime.datetime.now().strftime('%m-%d-%yT%H:%M:%S.%f')}"""  # noqa
                        )
                        new_primary_keys.append(new_primary_key.first()[0])
                    else:
                        logging.info("Did not return primary keys")
                        statement = table.insert().values(record)
                        logging.info(
                            f"Starting statement execution for record #{n_records} at:{datetime.datetime.now().strftime('%m-%d-%yT%H:%M:%S.%f')}"  # noqa
                        )
                        session.execute(statement)
                        logging.info(
                            f"""Done with statement execution
                              for record #{n_records} at: {datetime.datetime.now().strftime('%m-%d-%yT%H:%M:%S.%f')}"""  # noqa
                        )
        return new_primary_keys

    def bulk_insert_dict(
        self, records_with_table: dict, return_primary_keys: bool = False
    ) -> dict:
        """
        Perform a bulk insert operation on a table as defined
        by the 'table' element in the dictionary along with the
        record(s), a list of record(s) as dictionaries.  This
        allows for several inserts to occur for different tables
        along with a single or multiple records for each table.

        :param records_with_table: a dictionary that defines the
            the SQLAlchemy table name to insert into
            along with a list of dictionaries as records to
            insert into the specified table
            eg. {
            "patient": [{"patient_id": UUID()}],
            "address": [{"line_1": "1313 Mocking Bird Lane, "city": "Scranton"},]
            }
        :param return_primary_keys: boolean indicating if you want the inserted
            primary keys for the table returned or not, defaults to False
        :return: a dictionary that contains table names as keys
            along with a list of the primary keys, if requested.
        """
        return_results = {}
        statements = []
        with self.transaction() as session:
            logging.info(
                f"Starting session at: {datetime.datetime.now().strftime('%m-%d-%yT%H:%M:%S.%f')}"  # noqa
            )
            n_records = 0
            for table in self.TABLE_LIST:
                records = records_with_table.get(table.name)
                if records is not None:
                    new_primary_keys = []

                    if len(records) > 0 and table is not None:
                        logging.info(
                            f"Getting primary_key_column at:{datetime.datetime.now().strftime('%m-%d-%yT%H:%M:%S.%f')}"  # noqa
                        )
                        primary_key_column = table.primary_key.c[0]

                        for record in records:
                            n_records += 1
                            if return_primary_keys:
                                logging.info("Returned primary keys")
                                statement = (
                                    table.insert()
                                    .values(record)
                                    .returning(primary_key_column)
                                )
                                logging.info(
                                    f"""Starting statement execution getting
                                    new_primary_key for record #{n_records}at:
                                        {datetime.datetime.now().strftime('%m-%d-%yT%H:%M:%S.%f')}"""  # noqa
                                )
                                new_primary_key = session.execute(statement)
                                # TODO: I don't like this, but seems to
                                # be one of the only ways to get this to work
                                #  I have tried using the column name from the
                                # PK defined in the table and that doesn't work
                                logging.info(
                                    f""" Done with statement execution getting new_primary_key
                                    for record #{n_records} at: {datetime.datetime.now().strftime('%m-%d-%yT%H:%M:%S.%f')}"""  # noqa
                                )
                                new_primary_keys.append(new_primary_key.first()[0])
                            else:
                                logging.info("Did not return primary keys")
                                logging.info(
                                    f"""Starting statement creation for record #{n_records} at:
                                        {datetime.datetime.now().strftime('%m-%d-%yT%H:%M:%S.%f')}"""  # noqa
                                )

                                if "dob" in record:
                                    if record["dob"] is not None:
                                        record["dob"] = datetime.datetime.strptime(
                                            record["dob"], "%Y-%m-%d"
                                        )

                                statement = table.insert().values(**record)

                                statement = statement.compile(
                                    self.engine,
                                    compile_kwargs={
                                        "literal_binds": True,
                                        "render_postprocess": str,
                                    },
                                )
                                statement = str(statement)
                                statements.append(statement)
                                logging.info(
                                    f"""Done with statement creation for record #{n_records} at:
                                        {datetime.datetime.now().strftime('%m-%d-%yT%H:%M:%S.%f')}"""  # noqa
                                )

                    return_results[table.name] = {"primary_keys": new_primary_keys}

            if not return_primary_keys:
                statements = ";".join(statements)
                logging.info(
                    f"Starting INSERT statement execution at:{datetime.datetime.now().strftime('%m-%d-%yT%H:%M:%S.%f')}"  # noqa
                )
                session.execute(text(statements))
                logging.info(
                    f"Done with INSERT statement execution at:{datetime.datetime.now().strftime('%m-%d-%yT%H:%M:%S.%f')}"  # noqa
                )
        return return_results

    def select_results(
        self, select_statement: select, include_col_header: bool = True
    ) -> List[list]:
        """
        Perform a select query and add the results to a
        list of lists.  Then add the column header as the
        first row, in the list of lists if the
        'include_col_header' parameter is True.

        :param select_statement: the select statment to execute
        :param include_col_header: boolean value to indicate if
            one wants to include a top row of the column headers
            or not, defaults to True
        :return: List of lists of select results
        """
        list_results = [[]]
        logging.info(
            f"In select_results, starting new session at {datetime.datetime.now().strftime('%m-%d-%yT%H:%M:%S.%f')}"  # noqa
        )
        with self.transaction() as session:
            logging.info(
                f"Starting to execute statement to return results at: {datetime.datetime.now().strftime('%m-%d-%yT%H:%M:%S.%f')}"  # noqa
            )
            results = session.execute(select_statement)
            logging.info(
                f"Done executing statement to return results at: {datetime.datetime.now().strftime('%m-%d-%yT%H:%M:%S.%f')}"  # noqa
            )
            list_results = [list(row) for row in results]
            if include_col_header:
                list_results.insert(0, list(results.keys()))
        return list_results

    def get_session(self) -> scoped_session:
        """
        Get a session object

        this method returns a session object to the caller

        :return: SQLAlchemy scoped session
        """

        session = scoped_session(
            sessionmaker(bind=self.engine)
        )  # NOTE extra config can be implemented in this call to sessionmaker factory
        return session()

    def get_table_by_name(self, table_name: str) -> Table:
        """
        Get an SqlAlchemy ORM Table Object based upon the table
        name passed in.

        :param table_name: the name of the table you want to get.
        :return: SqlAlchemy ORM Table Object.
        """

        if table_name is not None and table_name != "":
            # TODO: I am sure there is an easier way to do this
            for table in self.TABLE_LIST:
                if table.name == table_name:
                    return table
        return None

    def get_table_by_column(self, column_name: str) -> Table:
        """
        Finds a table in the MPI based upon the column name.
        Note, this won't work if the column name used exists
        in more than one table.

        :param column_name: the column name you want to find the
            table it belongs to.
        :return: SqlAlchemy ORM Table Object.
        """

        if column_name is not None and column_name != "":
            # TODO: I am sure there is an easier way to do this
            for table in self.TABLE_LIST:
                if column_name in table.c:
                    return table
        return None

    def does_table_have_column(self, table: Table, column_name: str) -> bool:
        """
        Verifies if a column exists in a particular table

        :param table: the table object to verify if column exists
            within.
        :param column_name: the column name you want to verify.
        :return: True or False.
        """
        if table is None or column_name is None or column_name == "":
            return False
        else:
            return column_name in table.c
        
    def insert_configurations(self, table: Table, configurations: dict):
        """
        Inserts the values in the configurations to the respective table.
        """
        with self.transaction() as session:
            logging.info(f"Started the session to insert the configurations into the database")
            
            if table.name == "configurations":
                logging.info(f"The table being inserted: {table}")

                # Convert the 'thresholds' dictionary to a JSON-serializable format
                configurations['thresholds'] = json.dumps(configurations['thresholds'])

                # Insert into the table
                insert_stmt = table.insert().values(
                    name=configurations.get("name"),
                    belongingness_ratio=configurations.get("belongingness_ratio"),
                    thresholds=configurations.get("thresholds"),
                    created_at=datetime.datetime.now(),
                    updated_at=datetime.datetime.now()
                )
                result = session.execute(insert_stmt)
                session.commit()
                return result.inserted_primary_key[0]

    def save_configuration_to_db(
        self,
        configurations: dict,
        schema_name: str = "configurations",
    ) -> dict:
        """
        Save configuration settings for the data elements to the database.
        """
        logging.info(f"Started save_configuration_to_db at {datetime.datetime.now().strftime('%m-%d-%yT%H:%M:%S.%f')}")
        
        try:
            # Ensure the engine is initialized and connected
            if self.engine is None:
                dbsettings = load_mpi_env_vars_os()
                dbuser = dbsettings.get("user")
                dbname = dbsettings.get("dbname")
                dbpwd = dbsettings.get("password")
                dbhost = dbsettings.get("host")
                dbport = dbsettings.get("port")
                self.get_connection(
                    engine_url=f"postgresql+psycopg2://{dbuser}:{dbpwd}@{dbhost}:{dbport}/{dbname}",
                    pool_size=5,
                    max_overflow=10,
                )
            
            # Bind the MetaData to the engine
            if not hasattr(self.Meta, 'is_bound') or not self.Meta.is_bound:
                self.Meta.reflect(bind=self.engine)
            
            # Connect to the database
            self.connection = self.engine.connect()
            self.initialize_config_schema()

            # Get the configurations table
            config_table = Table(schema_name, self.Meta, autoload_with=self.engine)
            logging.info(f"Configuration Table initialized: {config_table}")

            # Insert configurations within the session context
            with self.get_session() as session:
                results = self.insert_configurations(config_table, configurations)
                session.commit()
                logging.info(f"Results inside save db config: {results}")
                return {"status": "success", "results": results}
            
        except Exception as e:
            logging.error(f"An error occurred while saving configuration to the database: {e}")
            return {"status": "error", "message": str(e)}


        
    def get_configurations(self) -> List[dict]:
        """
        Retrieve all configurations from the database.
        """
        try:
            if self.engine is None:
                raise ValueError("Database engine is not initialized.")
            config_table = Table("configurations", self.Meta, autoload_with=self.engine)
            with self.get_session() as session:
                query = session.query(config_table).all()
                return [dict(row) for row in query]
        except SQLAlchemyError as e:
            logging.error(f"An error occurred while retrieving configurations: {e}")
            return []
        

    def insert_pass_configuration(self, table, data):
        """
        Inserts a new pass configuration into the database.
        """
        required_fields = ["name", "description", "lowerbound", "upperbound", "matchingCriteria", "blocks", "status"]
        for field in required_fields:
            if field not in data:
                logging.error(f"Missing required field: {field}")
                raise ValueError(f"One or more required fields are missing from the data: {field} is required.")
        custom_id = generate_custom_id()
        logging.info(f"Custom ID: {custom_id}")
        insert_stmt = table.insert().values(
            id=custom_id,
            pass_name=data["name"],
            description=data["description"],
            lowerbound=data["lowerbound"],
            upperbound=data["upperbound"],
            matching_criteria=data["matchingCriteria"],
            blocks=data["blocks"],
            status=data["status"]
        ).returning(table.c.id)
        self.connection.execute(insert_stmt)
        self.connection.commit()
        pass_config_id = custom_id
        return pass_config_id

    def save_pass_config_to_db(
        self,
        configurations: dict,
        schema_name: str = "pass_configurations",
    ) -> dict:
        """
        Save pass configuration to the database.
        """
        logging.info(f"Started save_pass_config_to_db at {datetime.datetime.now().strftime('%m-%d-%yT%H:%M:%S.%f')}")
        
        # Log the configurations dictionary for debugging
        logging.debug(f"Configurations data: {configurations}")

        dbsettings = load_mpi_env_vars_os()
        dbuser = dbsettings.get("user")
        dbname = dbsettings.get("dbname")
        dbpwd = dbsettings.get("password")
        dbhost = dbsettings.get("host")
        dbport = dbsettings.get("port")
        self.get_connection(
            engine_url=f"postgresql+psycopg2://{dbuser}:"
            + f"{dbpwd}@{dbhost}:{dbport}/{dbname}",
            pool_size=5,
            max_overflow=10,
        )
        self.connection = self.engine.connect()
        self.initialize_pass_config_schema()
        try:
            if self.engine is None:
                raise ValueError("Database engine is not initialized.")
            
            config_table = Table(schema_name, self.Meta, autoload_with=self.engine)
            logging.info(f"Configuration Table initialized: {config_table}")
            
            # Insert the configuration into the database
            pass_config_id = self.insert_pass_configuration(config_table, configurations)
            
            logging.info(f"Pass configuration saved with ID: {pass_config_id}")
            
            # Return the formatted response
            return {
                "message": "You have successfully saved the pass configuration",
                "pass_config_id": pass_config_id,
                "active": configurations.get("status", "Inactive"),  # Safely get status
                "name": configurations["name"]
            }
        except Exception as e:
            logging.error(f"An error occurred while saving pass configuration to the database: {e}")
            return {"status": "error", "message": str(e)}
