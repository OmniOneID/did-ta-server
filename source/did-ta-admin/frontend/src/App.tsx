import DashboardIcon from '@mui/icons-material/Dashboard';
import type { Navigation, Session } from '@toolpad/core/AppProvider';
import { ReactRouterAppProvider } from '@toolpad/core/react-router';
import { DialogsProvider } from '@toolpad/core/useDialogs';
import React from 'react';
import { Outlet, useNavigate } from 'react-router';
import { SessionContext } from './context/SessionContext';

// Sidebar menu link
const NAVIGATION: Navigation = [
  {
    segment: 'ta-register',
    title: 'TA 등록',
    icon: <DashboardIcon />,
  },
];

export default function App() {
  const navigate = useNavigate();

  const [session, setSessionState] = React.useState<Session | null>(() => {
    const storedSession = localStorage.getItem('session');
    return storedSession ? JSON.parse(storedSession) : null;
  });

  const [navigation, setNavigation] = React.useState<Navigation>(NAVIGATION);

  const setSession = React.useCallback((newSession: Session | null) => {
    setSessionState(newSession);
    if (newSession) {
      localStorage.setItem('session', JSON.stringify(newSession));
    } else {
      localStorage.removeItem('session'); 
    }
  }, []);

  const signIn = React.useCallback(() => {
    navigate('/sign-in');
  }, [navigate]);

  const signOut = React.useCallback(() => {
    setSession(null);
    navigate('/sign-in');
  }, [navigate]);

  const sessionContextValue = React.useMemo(() => ({ session, setSession }), [session, setSession]);

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
