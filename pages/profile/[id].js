import { useRouter } from 'next/router';


const Profile = () => {
    const router = useRouter();
    const { userId, username, email } = router.query; 


    return (
        <div>
            <h1>صفحه پروفایل</h1>
            <p>userId: {userId}</p>
            <p>username: {username.trim()}</p>
            <p>email: {email.trim()}</p>
        </div>
    );
};

export default Profile;