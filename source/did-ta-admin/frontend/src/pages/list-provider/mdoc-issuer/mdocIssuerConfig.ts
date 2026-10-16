import type { MdocIssuerStatus } from "../../../apis/models/MdocIssuer";

export const statusConfig: Record<MdocIssuerStatus, { label: string; color: string }> = {
  ACTIVE: { label: "Active", color: "#2e7d32" },
  SUSPENDED: { label: "Suspended", color: "#9c6b00" },
  REVOKED: { label: "Revoked", color: "#616161" },
};

export const allowedStatusActions: Record<MdocIssuerStatus, MdocIssuerStatus[]> = {
  ACTIVE: ["SUSPENDED", "REVOKED"],
  SUSPENDED: ["ACTIVE", "REVOKED"],
  REVOKED: [],
};

export const statusActionLabel: Record<MdocIssuerStatus, string> = {
  ACTIVE: "Reactivate",
  SUSPENDED: "Suspend",
  REVOKED: "Revoke",
};

export const requiresReason = (status: MdocIssuerStatus) =>
  status === "SUSPENDED" || status === "REVOKED";

/** Display names for well-known docTypes; any other docType is shown as is. */
const KNOWN_DOC_TYPES: Record<string, string> = {
  "org.iso.18013.5.1.mDL": "mDL",
  "eu.europa.ec.eudi.pid.1": "EU PID",
};

export const docTypeLabel = (value: string) =>
  KNOWN_DOC_TYPES[value] ? `${KNOWN_DOC_TYPES[value]} · ${value}` : value;
