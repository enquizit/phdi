from enum import Enum
from typing import Self
from linkage.models.identification_types import IdentificationType
from dataclasses import dataclass, field


@dataclass
class Identification:
    type: IdentificationType | str
    value: str
    assigning_authority: str


@dataclass
class Name:
    use: str | None
    family: str | None
    suffix: str | None
    given: list[str] = field(default_factory=list)


@dataclass
class Address:
    city: str | None
    state: str | None
    zip: str | None
    street: list[str] = field(default_factory=list)


class System(Enum):
    PHONE = "phone"
    EMAIL = "email"

    @staticmethod
    def from_str(value: str | None) -> Self | None:
        if value is None:
            return None
        else:
            match value.lower():
                case "phone":
                    return System.PHONE
                case "email":
                    return System.EMAIL
                case _:
                    return None


@dataclass
class Telecom:
    value: str
    system: System | None


@dataclass
class Patient:
    birthdate: str | None
    sex: str | None
    name: Name
    address: Address
    telecom: list[Telecom] = field(default_factory=list)
    identifications: list[Identification] = field(default_factory=list)
    patient_id: str | None = None
    person_id: str | None = None

    def get_first_name(self) -> str | None:
        if self.name is not None and len(self.name.given) > 0:
            return self.name.given[0]
        return None

    def get_middle_name(self) -> str | None:
        if self.name is not None and len(self.name.given) > 1:
            return self.name.given[1]
        return None

    def get_second_middle_name(self) -> str | None:
        if self.name is not None and len(self.name.given) > 2:
            return self.name.given[2]
        return None

    def get_last_name(self) -> str | None:
        if self.name is not None:
            return self.name.family
        return None

    def get_suffix(self) -> str | None:
        if self.name is not None:
            return self.name.suffix
        return None

    def get_sex(self) -> str | None:
        if self.sex is not None and len(self.sex) > 0:
            return self.sex[0].lower()
        return None

    def get_phone_number(self) -> str | None:
        telecoms = [x for x in self.telecom if x.system == System.PHONE]
        return telecoms[0].value if len(telecoms) > 0 else None

    def get_street_address(self) -> str | None:
        if self.address is not None and len(self.address.street) > 0:
            return self.address.street[0]
        return None

    def get_city(self) -> str | None:
        if self.address is not None:
            return self.address.city
        return None

    def get_state(self) -> str | None:
        if self.address is not None:
            return self.address.state
        return None

    def get_zip(self) -> str | None:
        if self.address is not None:
            return self.address.zip
        return None

    def get_id(self, id_type: str) -> Identification | None:
        id_list = [x for x in self.identifications if x.type == id_type]
        return id_list[0] if len(id_list) > 0 else None
