import Head from 'next/head';
import FileUploadForm from '../components/FileUploadForm';

export default function Home() {
    return (
        <div>
            <Head>
                <title>File Upload App</title>
                <meta name="description" content="A simple file upload application"/>
            </Head>
            <main>
                <FileUploadForm/>
            </main>
        </div>
    );
}
