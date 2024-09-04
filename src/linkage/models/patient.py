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


@dataclass
class Patient:
    birthdate: str | None
    gender: str | None
    name: Name
    address: Address
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
