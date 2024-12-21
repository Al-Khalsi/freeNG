import PreventDevTools from '@/components/modules/PreventDevTools';
import { AuthProvider } from '../context/AuthContext';
import { ImageProvider } from '@/context/ImageContext'; 
import { useAuth } from '@/context/AuthContext';
import "../styles/globals.css";


function App({ Component, pageProps }) {
  return (
    <AuthProvider>
      <AuthConsumer />
      <ImageProvider>
        <Component {...pageProps} />
      </ImageProvider>
    </AuthProvider>
  );
}

function AuthConsumer() {
  const { role } = useAuth();
  return (
    <>
      {role !== 'ROLE_MASTER' && <PreventDevTools />}
    </>
  );
}

export default App;

