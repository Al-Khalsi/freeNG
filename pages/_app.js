import PreventDevTools from '@/components/modules/PreventDevTools';
import { AuthProvider } from '../context/AuthContext'; 
import "../styles/globals.css";


function App({ Component, pageProps }) {
  return (
    <AuthProvider>
      <PreventDevTools />
      <Component {...pageProps} />
    </AuthProvider>
  )

}

export default App;

