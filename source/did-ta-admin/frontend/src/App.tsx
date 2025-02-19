import DashboardIcon from '@mui/icons-material/Dashboard';
import type { Navigation, Session } from '@toolpad/core/AppProvider';
import { ReactRouterAppProvider } from '@toolpad/core/react-router';
import { DialogsProvider } from '@toolpad/core/useDialogs';
import React, { useEffect, useState, useCallback, useMemo } from 'react';
import { Outlet, useNavigate } from 'react-router';
import { SessionContext } from './context/SessionContext';
import { ServerStatusProvider, useServerStatus } from './context/ServerStatusContext';
import LoadingScreen from './components/loading/LoadingScreen';
import { getTaInfo } from './apis/TaApi';
import { getNavigationByStatus } from './config/navigationConfig';

function AppContent() {
  const navigate = useNavigate();
  
  const { serverStatus, loading, setServerStatus, setLoading } = useServerStatus();

  const [session, setSessionState] = useState<Session | null>(() => {
    const storedSession = localStorage.getItem('session');
    return storedSession ? JSON.parse(storedSession) : null;
  });

  const [navigation, setNavigation] = useState<Navigation>(getNavigationByStatus(null));

  const setSession = useCallback((newSession: Session | null) => {
    setSessionState(newSession);
    if (newSession) {
      localStorage.setItem('session', JSON.stringify(newSession));
    } else {
      localStorage.removeItem('session'); 
    }
  }, []);

  const signIn = useCallback(() => {
    navigate('/sign-in');
  }, [navigate]);

  const signOut = useCallback(() => {
    setSession(null);
    navigate('/sign-in');
  }, [navigate]);

  useEffect(() => {
    getTaInfo()
      .then(({ url, data }) => {
        setServerStatus(data.status);
        setNavigation(getNavigationByStatus(data.status));
        setLoading(false);
      })
      .catch((err) => {
        console.error('TA 정보 조회 실패:', err);
        navigate('/sign-in');
      });

  }, [setServerStatus]);

  useEffect(() => {
    if (serverStatus !== 'COMPLETED') {
      navigate('/');
    } else {
      navigate('/ta-register');
    }
  }, [serverStatus, navigate]);

  const sessionContextValue = useMemo(() => ({ session, setSession }), [session, setSession]);

  if (loading) {
    return <LoadingScreen />;
  }

  return (
    <SessionContext.Provider value={sessionContextValue}>
      <DialogsProvider>
        <ReactRouterAppProvider
          navigation={navigation}
          session={session}
          authentication={{ signIn, signOut }}
        >
          <Outlet />
        </ReactRouterAppProvider>
      </DialogsProvider>
    </SessionContext.Provider>
  );
}

export default function App() {
  return (
    <ServerStatusProvider>
      <AppContent />
    </ServerStatusProvider>
  );
}
