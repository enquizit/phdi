import { DisplayData } from "../../utils";
import { Fragment } from "react";

import {
  AccordianSection,
  AccordianH3,
  AccordianDiv,
} from "../component-utils";
import { SectionConfig } from "./SideNav";

interface EcrMetadataProps {
  rrDetails: DisplayData[];
  eicrDetails: DisplayData[];
  eCRSenderDetails: DisplayData[];
}

export const ecrMetadataConfig: SectionConfig = new SectionConfig(
  "eCR Metadata",
  ["RR Details", "eICR Details", "eCR Sender Details"],
);

const EcrMetadata = ({
  rrDetails,
  eicrDetails,
  eCRSenderDetails,
}: EcrMetadataProps) => {
  return (
    <AccordianSection>
      <AccordianH3>
        <span id={ecrMetadataConfig.subNavItems?.[0].id}>
          {ecrMetadataConfig.subNavItems?.[0].title}
        </span>
      </AccordianH3>
      <AccordianDiv>
        {rrDetails.map(({ title, value }) => {
          return (
            <Fragment key={title}>
              <div className="grid-row">
                <div className="data-title">
                  <h4>{title}</h4>
                </div>
                <div className="grid-col-fill text-pre-line">{value}</div>
              </div>
              <div className={"section__line_gray"} />
            </Fragment>
          );
        })}
        <div className={"padding-bottom-1"} />
        <AccordianH3>
          <span id={ecrMetadataConfig.subNavItems?.[1].id}>
            {ecrMetadataConfig.subNavItems?.[1].title}
          </span>
        </AccordianH3>
        {eicrDetails.map(({ title, value }) => {
          return (
            <Fragment key={title}>
              <div className="grid-row">
                <div className="data-title">
                  <h4>{title}</h4>
                </div>
                <div className="grid-col-auto text-pre-line">{value}</div>
              </div>
              <div className={"section__line_gray"} />
            </Fragment>
          );
        })}
        <div className={"padding-bottom-1"} />
        <AccordianH3>
          <span id={ecrMetadataConfig.subNavItems?.[2].id}>
            {ecrMetadataConfig.subNavItems?.[2].title}
          </span>
        </AccordianH3>
        {eCRSenderDetails.map(({ title, value }) => {
          return (
            <Fragment key={title}>
              <div className="grid-row">
                <div className="data-title">
                  <h4>{title}</h4>
                </div>
                <div className="grid-col-auto text-pre-line">{value}</div>
              </div>
              <div className={"section__line_gray"} />
            </Fragment>
          );
        })}
      </AccordianDiv>
    </AccordianSection>
  );
};

export default EcrMetadata;
