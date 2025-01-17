import abc

from app.DAL.PostgresFhirDataModel import PostgresFhirDataModel


class FhirRepository(metaclass=abc.ABCMeta):
    @abc.abstractmethod
    def persist(self, entity: PostgresFhirDataModel):
        """
        Intended to merge and commit a FHIR data model entity to the database.
        Currently not implemented.

        :param entity: The FHIR data model entity to be persisted.
        :raises NotImplementedError: Indicates the method is not yet
            implemented.
        """
        raise NotImplementedError()
