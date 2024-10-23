import { useRouter } from 'next/router';

const Product = () => {
    const router = useRouter();
    
    const { id } = router.query;

    return (
        <div>
            <h1>page product</h1>
            <p>This page is about product number {id}.</p>
        </div>
    );
};

export default Product;