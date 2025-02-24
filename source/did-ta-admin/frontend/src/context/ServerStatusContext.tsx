import React, { createContext, useContext, useState, ReactNode, useCallback } from 'react';
import { TaInfoResDto } from '../apis/models/TaInfoResDto';

export type ServerStatus = 'DID_DOCUMENT_REQUIRED' | 'CERTIFICATE_VC_REQUIRED' | 'COMPLETED';

interface ServerStatusContextType {
  serverStatus: ServerStatus | null;
  setServerStatus: (status: ServerStatus | null) => void;
  isLoading: boolean;
  setIsLoading: (loading: boolean, message?: string) => void;
  isLoadingMessage: string;
  taInfo: TaInfoResDto | null;
  setTaInfo: (info: TaInfoResDto | null) => void;
}

export const ServerStatusContext = createContext<ServerStatusContextType>({
  serverStatus: null,
  setServerStatus: () => {},
  isLoading: false,
  setIsLoading: () => {},
  isLoadingMessage: '',
  taInfo: null,
  setTaInfo: () => {},
});

export const ServerStatusProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [serverStatus, setServerStatus] = useState<ServerStatus | null>(null);
  const [isLoading, setIsLoadingState] = useState<boolean>(false);
  const [isLoadingMessage, setIsLoadingMessage] = useState<string>('');
  const [taInfo, setTaInfo] = useState<TaInfoResDto | null>(null);

  // ✅ useCallback을 사용하여 setIsLoading의 참조를 고정 (무한 렌더링 방지)
  const setIsLoading = useCallback((loading: boolean, message?: string) => {
    setIsLoadingState(loading);
    setIsLoadingMessage(message ?? '처리 중입니다...');
  }, []); // 🔹 의존성 배열을 빈 배열로 설정하여 함수 참조 유지

  return (
    <ServerStatusContext.Provider 
    value={{ 
        serverStatus, 
        setServerStatus, 
        isLoading, 
        setIsLoading, 
        isLoadingMessage,
        taInfo,
        setTaInfo,
      }}
    >
      {children}
    </ServerStatusContext.Provider>
  );
};

export const useServerStatus = () => useContext(ServerStatusContext);
