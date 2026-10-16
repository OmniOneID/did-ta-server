export const MDOC_ISSUER_STATUSES = ["ACTIVE", "SUSPENDED", "REVOKED"] as const;

export type MdocIssuerStatus = typeof MDOC_ISSUER_STATUSES[number];

export interface MdocIssuer {
  id: number;
  entityId: number;
  did: string | null;
  name: string | null;
  metadataUri: string | null;
  docTypes: string[];
  status: MdocIssuerStatus;
  createdAt: string;
  updatedAt: string | null;
}

export interface RegisterMdocIssuerRequest {
  entityId: number;
  metadataUri: string;
  docTypes: string[];
}

/** mso_mdoc docTypes the issuer declares in its OID4VCI metadata. */
export interface MdocIssuerMetadata {
  metadataUri: string;
  credentialIssuer: string | null;
  docTypes: string[];
}

export interface MdocIssuerStatusHistory {
  id: number;
  previousStatus: MdocIssuerStatus | null;
  newStatus: MdocIssuerStatus;
  reason: string | null;
  changedBy: string | null;
  changedAt: string;
}
