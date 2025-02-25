// utils/withAuthRedirect.js
import { useEffect } from 'react';
import { useRouter } from 'next/router';
import { useAuth } from '../context/AuthContext';

const withAuthRedirect = (WrappedComponent) => {
    return (props) => {
        const { token } = useAuth();
        const router = useRouter();

        useEffect(() => {
            if (token) {
                router.push('/');
            }
        }, [token, router]);

        return <WrappedComponent {...props} />;
    };
};

export default withAuthRedirect;