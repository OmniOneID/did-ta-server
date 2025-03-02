import { useEffect } from 'react';
import { useNavigate } from 'react-router';

type Props = {}

const NotificationProviderPage = (props: Props) => {
  const navigate = useNavigate();

  useEffect(() => {
    navigate('noti-settings/email-server');
  }, [navigate]);

  return null;
}

export default NotificationProviderPage