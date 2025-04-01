import { useEffect } from 'react';
import { useNavigate } from 'react-router';


type Props = {}

const ListProviderPage = (props: Props) => {
    const navigate = useNavigate();

    useEffect(() => {
      navigate('list-settings/allowed-ca');
    }, [navigate]);
  
    return null;
}

export default ListProviderPage