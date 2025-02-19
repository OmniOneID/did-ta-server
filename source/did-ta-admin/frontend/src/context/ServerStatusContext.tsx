import React, { createContext, useContext, useState, ReactNode } from 'react';

export type ServerStatus = 'DID_DOCUMENT_REQUIRED' | 'CERTIFICATE_VC_REQUIRED' | 'COMPLETED';

interface ServerStatusContextType {
  serverStatus: ServerStatus | null;
  loading: boolean;
  setServerStatus: (status: ServerStatus | null) => void;
  setLoading: (loading: boolean) => void; 
}

export const ServerStatusContext = createContext<ServerStatusContextType>({
  serverStatus: null,
  loading: true, 
  setServerStatus: () => {}, 
  setLoading: () => {}, 
});

export const ServerStatusProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [serverStatus, setServerStatus] = useState<ServerStatus | null>(null);
  const [loading, setLoading] = useState(true);

  return (
    <ServerStatusContext.Provider value={{ serverStatus, loading, setServerStatus, setLoading }}>
      {children}
    </ServerStatusContext.Provider>
  );
};

export const useServerStatus = () => useContext(ServerStatusContext);
