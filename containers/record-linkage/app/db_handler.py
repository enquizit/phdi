import datetime
from logging import Handler
import traceback

from app.utils import run_migrations
from app.linkage.mpi import DIBBsMPIConnectorClient

class DBHandler(Handler):

    
    def __init__(self )-> None:
        Handler.__init__(self=self)
        run_migrations()
        self.mpi_client = DIBBsMPIConnectorClient()  # noqa: F841
        
    
    # A very basic logger that commits a LogRecord to the SQL Db
    def emit(self, record):
        if ('Request ID:' in record.msg):
           exc = record.exc_info
           if exc:
               log_record["trace"] = traceback.format_exc()
           log_record = {}
           log_record["logger"]=record.name
           log_record["log_level"]=record.levelname
           log_record["msg"]=record.msg
           log_record["created_at"]=datetime.datetime.now()
           dbSession = self.mpi_client.dal.get_session()
           dbSession.execute(self.mpi_client.dal.LOG_ITEM_TABLE.insert(), [log_record])
           dbSession.commit()
        