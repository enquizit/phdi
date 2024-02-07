import { DisplayData } from "@/app/utils";
import { AccordianSection } from "../component-utils";

interface UnavailableInfoProps {
  demographicsUnavailableData: DisplayData[];
  socialUnavailableData: DisplayData[];
  encounterUnavailableData: DisplayData[];
  providerUnavailableData: DisplayData[];
  reasonUnavailableData: DisplayData[];
  activeProblemsUnavailableData: DisplayData[];
  vitalUnavailableData: DisplayData[];
}

const UnavailableInfo = ({
  demographicsUnavailableData,
  socialUnavailableData,
  encounterUnavailableData,
  providerUnavailableData,
  activeProblemsUnavailableData,
  vitalUnavailableData,
}: UnavailableInfoProps) => {
  const renderRow = (item: any, index: number) => {
    return (
      <div key={index}>
        <div className="grid-row">
          <div className="data-title font-sans-xs">
            <h4>{item.title}</h4>
          </div>
          <div className="grid-col-auto font-sans-xs">N/A</div>
        </div>
        <div className={"section__line_gray"} />
      </div>
    );
  };
  const renderSection = (sectionTitle: string, data: DisplayData[]) => {
    return (
      <div className="margin-bottom-4">
        <h3
          className="usa-summary-box__heading padding-bottom-205"
          id="summary-box-key-information"
        >
          {sectionTitle}
        </h3>
        <div className="usa-summary-box__text">
          {data.map((item, index) => renderRow(item, index))}
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
      {providerUnavailableData.length > 0 &&
        renderSection("Provider Details", providerUnavailableData)}
      {activeProblemsUnavailableData?.length > 0 && // Add other items under symptoms and problems here
        renderSection("Symptoms and Problems", activeProblemsUnavailableData)}
      {vitalUnavailableData?.length > 0 && // Add other items under symptoms and problems here
        renderSection("Diagnostics and Vital Signs", vitalUnavailableData)}
    </AccordianSection>
  );
};

export default UnavailableInfo;
