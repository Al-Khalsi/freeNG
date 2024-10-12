import Link from 'next/link';

function index() {
  return (
    <div>
      <h1>صفحه اصلی</h1>
      <Link href="/product/1">به صفحه محصول 1 بروید</Link>
      <br />
      <Link href="/product/2">به صفحه محصول 2 بروید</Link>
    </div>
  )
}

export default index