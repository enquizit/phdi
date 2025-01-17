import { DataDisplay, DisplayData } from "@/app/utils";
import { AccordianSection } from "../component-utils";
import React from "react";

interface UnavailableInfoProps {
  demographicsUnavailableData: DisplayData[];
  socialUnavailableData: DisplayData[];
  encounterUnavailableData: DisplayData[];
  providerUnavailableData: DisplayData[];
  reasonForVisitUnavailableData: DisplayData[];
  activeProblemsUnavailableData: DisplayData[];
  vitalUnavailableData: DisplayData[];
  treatmentData: DisplayData[];
  clinicalNotesData: DisplayData[];
  immunizationsUnavailableData: DisplayData[];
}

/**
 * Function component for displaying data that is unavailable in the eCR viewer.
 * @param props The properties for unavailable information
 * @param props.demographicsUnavailableData The unavailable demographic data
 * @param props.socialUnavailableData The unavailable social data
 * @param props.encounterUnavailableData The unavailable encounter data
 * @param props.providerUnavailableData The unavailable provider data
 * @param props.reasonForVisitUnavailableData The unavailable reason for visit data
 * @param props.activeProblemsUnavailableData The unavailable active problems data
 * @param props.immunizationsUnavailableData The unavailable immunizations data
 * @param props.vitalUnavailableData The unavailable vital data
 * @param props.treatmentData The unavailable treatment data
 * @param props.clinicalNotesData The unavailable clinical notes
 * @returns The JSX element representing all unavailable data.
 */
const UnavailableInfo: React.FC<UnavailableInfoProps> = ({
  demographicsUnavailableData,
  socialUnavailableData,
  encounterUnavailableData,
  providerUnavailableData,
  reasonForVisitUnavailableData,
  activeProblemsUnavailableData,
  immunizationsUnavailableData,
  vitalUnavailableData,
  treatmentData,
  clinicalNotesData,
}) => {
  const renderSection = (sectionTitle: string, data: DisplayData[]) => {
    return (
      <div className="margin-bottom-4">
        <h3 className="usa-summary-box__heading padding-bottom-205 unavailable-info">
          {sectionTitle}
        </h3>
        <div className="usa-summary-box__text">
          {data.map((item, index) => (
            <DataDisplay
              item={{ ...item, value: "No data" }}
              className={"text-italic text-base"}
              key={index}
            />
          ))}
        </div>
      </div>
    );
  };

  return (
    <AccordianSection>
      {demographicsUnavailableData?.length > 0 &&
        renderSection("Demographics", demographicsUnavailableData)}
      {socialUnavailableData?.length > 0 &&
        renderSection("Social History", socialUnavailableData)}
      {encounterUnavailableData?.length > 0 &&
        renderSection("Encounter Details", encounterUnavailableData)}
      {clinicalNotesData?.length > 0 &&
        renderSection("Clinical Notes", clinicalNotesData)}
      {providerUnavailableData.length > 0 &&
        renderSection("Provider Details", providerUnavailableData)}
      {(reasonForVisitUnavailableData?.length > 0 ||
        activeProblemsUnavailableData?.length > 0) &&
        renderSection("Symptoms and Problems", activeProblemsUnavailableData)}
      {vitalUnavailableData?.length > 0 &&
        renderSection("Diagnostics and Vital Signs", vitalUnavailableData)}
      {immunizationsUnavailableData?.length > 0 &&
        renderSection("Immunizations", immunizationsUnavailableData)}
      {treatmentData?.length > 0 &&
        renderSection("Treatment Details", treatmentData)}
    </AccordianSection>
  );
};

export default UnavailableInfo;
