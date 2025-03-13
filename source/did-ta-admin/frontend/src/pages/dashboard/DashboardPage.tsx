import React from 'react'
import { Navigate } from 'react-router';
import { useServerStatus } from '../../context/ServerStatusContext';

type Props = {}

const DashboardPage = (props: Props) => {
    const { setServerStatus, serverStatus } = useServerStatus();

    if (serverStatus !== 'COMPLETED') {
        return <Navigate to="/ta-registration" replace />;
    } else {
        return <Navigate to="/ta-management" replace />;
    }
    
}

export default DashboardPage