import { AuthProvider } from '../context/AuthContext'; // Adjust the path as necessary
import "../styles/globals.css";
import 'react-notifications/lib/notifications.css';


function App({ Component, pageProps }) {
  return (
    <AuthProvider>
      <Component {...pageProps} />
    </AuthProvider>
  )

}

export default App;

