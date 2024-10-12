import Link from 'next/link';

function index() {
  return (
    <div>
      <h1>home page</h1>
      <Link href="/product/1">Go to product page 1</Link>
      <br />
      <Link href="/product/2">Go to product page 2</Link>
    </div>
  )
}

export default index