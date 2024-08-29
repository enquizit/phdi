from abc import ABC
from abc import abstractmethod
from linkage.models.patient import Patient
from linkage.models.configuration import BlockCriteria


class BaseMPIConnectorClient(ABC):

    @abstractmethod
    def get_patient_data(
        self, patient: Patient, criteria: list[BlockCriteria]
    ) -> list[Patient]:

        pass
