import React, { createContext, useContext, useState, ReactNode } from 'react';

export type ServerStatus = 'DID_DOCUMENT_REQUIRED' | 'CERTIFICATE_VC_REQUIRED' | 'COMPLETED';

interface ServerStatusContextType {
  serverStatus: ServerStatus | null;
  setServerStatus: (status: ServerStatus | null) => void;
}

export const ServerStatusContext = createContext<ServerStatusContextType>({
  serverStatus: null,
  setServerStatus: () => {}, 
});

export const ServerStatusProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [serverStatus, setServerStatus] = useState<ServerStatus | null>(null);

  return (
    <ServerStatusContext.Provider value={{ serverStatus, setServerStatus }}>
      {children}
    </ServerStatusContext.Provider>
  );
};

export const useServerStatus = () => useContext(ServerStatusContext);
