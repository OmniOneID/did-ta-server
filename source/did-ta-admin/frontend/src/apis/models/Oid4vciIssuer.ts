export const OID4VCI_ISSUER_STATUSES = [
  "REQUESTED", "ACTIVE", "REJECTED", "SUSPENDED", "REVOKED",
] as const;

export type Oid4vciIssuerStatus = typeof OID4VCI_ISSUER_STATUSES[number];

export interface Oid4vciIssuerForm {
  credentialIssuer: string;
  credentialIssuerMetadataUri: string;
  userInitiationUri: string;
}

export interface Oid4vciIssuer extends Oid4vciIssuerForm {
  id: number;
  status: Oid4vciIssuerStatus;
  createdAt: string;
  updatedAt: string | null;
}

export interface Oid4vciStatusHistory {
  id: number;
  previousStatus: Oid4vciIssuerStatus | null;
  newStatus: Oid4vciIssuerStatus;
  reason: string | null;
  changedBy: string | null;
  changedAt: string;
}
