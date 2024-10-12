import { useRouter } from 'next/router';

const Product = () => {
    const router = useRouter();
    const { id } = router.query; // دریافت id از پارامترهای URL

    return (
        <div>
            <h1>صفحه محصول</h1>
            <p>این صفحه مربوط به محصول شماره {id} است.</p>
        </div>
    );
};

export default Product;