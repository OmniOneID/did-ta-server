import { CssBaseline, GlobalStyles, useTheme } from '@mui/material';
import type { Navigation, Session } from '@toolpad/core/AppProvider';
import { ReactRouterAppProvider } from '@toolpad/core/react-router';
import { DialogsProvider } from '@toolpad/core/useDialogs';
import { useCallback, useEffect, useMemo, useState } from 'react';
import { Outlet, useNavigate } from 'react-router';
import { getTaInfo } from './apis/ta-api';
import LoadingOverlay from './components/loading/LoadingOverlay';
import { getNavigationByStatus } from './config/navigationConfig';
import { ServerStatusProvider, useServerStatus } from './context/ServerStatusContext';
import { ExtendedSession, SessionContext } from './context/SessionContext';
import customTheme from './theme';
import { formatErrorMessage } from './utils/error-handler';

function AppContent() {
  const navigate = useNavigate();
  
  const { serverStatus, setServerStatus, setTaInfo } = useServerStatus();
  const [isLoading, setIsLoading] = useState(true);

  const [session, setSessionState] = useState<ExtendedSession | null>(() => {
    const storedSession = localStorage.getItem('session');
    return storedSession ? JSON.parse(storedSession) : null;
  });

  const [navigation, setNavigation] = useState<Navigation>(getNavigationByStatus(null));

  const theme = useTheme(); 
  
  const setSession = useCallback((newSession: ExtendedSession | null) => {
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

  // Fetch TA information
  useEffect(() => {
    const fetchTaInfo = () => {
      setIsLoading(true);
      getTaInfo()
        .then(({ data }) => {
          setServerStatus(data.status);
          setTaInfo(data);
          setNavigation(getNavigationByStatus(data.status));
          setIsLoading(false);
        })
        .catch((err) => {
          navigate('/error', { state: { message: formatErrorMessage(err, "Failed to fetch Trust Agent Information") } });
          setIsLoading(false);
        });
    };

    fetchTaInfo();

    const handlePopState = (event: PopStateEvent) => {
      fetchTaInfo();
    };
    window.addEventListener('popstate', handlePopState);

    return () => {
      window.removeEventListener('popstate', handlePopState);
    };
  }, []);

  useEffect(() => {
    if (serverStatus !== null) {
      setNavigation(getNavigationByStatus(serverStatus));
    }
  }, [serverStatus]);

  const sessionContextValue = useMemo(() => ({ session, setSession }), [session, setSession]);

  if (isLoading) {
    return <LoadingOverlay />;
  }

  return (
    <SessionContext.Provider value={sessionContextValue}>
      <DialogsProvider>
        <ReactRouterAppProvider
          navigation={navigation}
          session={session}
          authentication={{ signIn, signOut }}
          theme={customTheme}
        >
          <CssBaseline />
          <Outlet />
        </ReactRouterAppProvider>
      </DialogsProvider>
    </SessionContext.Provider>
  );
}

export default function App() {
  return (
    <ServerStatusProvider>
      <GlobalStyles styles={{ body: { padding: "10px" } }} />
      <AppContent />
    </ServerStatusProvider>
  );
}
