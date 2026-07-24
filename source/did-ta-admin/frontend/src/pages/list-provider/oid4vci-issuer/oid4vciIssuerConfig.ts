import type { Oid4vciIssuerStatus } from "../../../apis/models/Oid4vciIssuer";

export const statusConfig: Record<Oid4vciIssuerStatus, { label: string; color: string }> = {
  REQUESTED: { label: "Requested", color: "#ed6c02" },
  ACTIVE: { label: "Active", color: "#2e7d32" },
  REJECTED: { label: "Rejected", color: "#d32f2f" },
  SUSPENDED: { label: "Suspended", color: "#9c6b00" },
  REVOKED: { label: "Revoked", color: "#616161" },
};

export const allowedStatusActions: Record<Oid4vciIssuerStatus, Oid4vciIssuerStatus[]> = {
  REQUESTED: ["ACTIVE", "REJECTED"],
  ACTIVE: ["SUSPENDED", "REVOKED"],
  SUSPENDED: ["ACTIVE", "REVOKED"],
  REJECTED: [],
  REVOKED: [],
};

export const statusActionLabel: Record<Oid4vciIssuerStatus, string> = {
  REQUESTED: "Request",
  ACTIVE: "Approve / Reactivate",
  REJECTED: "Reject",
  SUSPENDED: "Suspend",
  REVOKED: "Revoke",
};

export const requiresReason = (status: Oid4vciIssuerStatus) =>
  status === "REJECTED" || status === "SUSPENDED" || status === "REVOKED";

export const defaultMetadataUri = (credentialIssuer: string) =>
  credentialIssuer.startsWith("did:")
    ? ""
    : `${credentialIssuer.replace(/\/+$/, "")}/.well-known/openid-credential-issuer`;

const DID_PATTERN = /^did:[a-z0-9]+:[A-Za-z0-9._:%-]+(?::[A-Za-z0-9._:%-]+)*$/;

const parseWebUri = (value: string) => {
  try {
    const uri = new URL(value);
    return (uri.protocol === "https:" || uri.protocol === "http:")
      && Boolean(uri.hostname) && !uri.username && !uri.password;
  } catch {
    return false;
  }
};

export const validateIssuerForm = (form: {
  credentialIssuer: string;
  credentialIssuerMetadataUri: string;
  userInitiationUri: string;
}) => {
  const errors: Partial<Record<keyof typeof form, string>> = {};
  if (!form.credentialIssuer.trim()) errors.credentialIssuer = "Credential Issuer is required.";
  else if (DID_PATTERN.test(form.credentialIssuer.trim())) {
    // A DID is a valid logical Credential Issuer identifier.
  } else if (!parseWebUri(form.credentialIssuer)) {
    errors.credentialIssuer = "Enter a valid DID, HTTP URI, or HTTPS URI.";
  } else {
    const uri = new URL(form.credentialIssuer);
    if (uri.search || uri.hash) errors.credentialIssuer = "Query and fragment are not allowed.";
  }
  if (!parseWebUri(form.credentialIssuerMetadataUri))
    errors.credentialIssuerMetadataUri = form.credentialIssuer.trim().startsWith("did:")
      ? "Metadata URI is required for a DID Credential Issuer and must be HTTP or HTTPS."
      : "Enter a valid HTTP or HTTPS Metadata URI.";
  if (!parseWebUri(form.userInitiationUri))
    errors.userInitiationUri = "Enter a valid HTTP or HTTPS User Initiation URI.";
  return errors;
};
