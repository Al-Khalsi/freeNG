import Header from '@/components/templates/Header';
import Footer from '@/components/templates/Footer';
import PageTitle from '@/components/templates/PageTitle';
import Pagination from '@/components/templates/Pagination';
import Link from 'next/link';

const MainLayout = ({
    children,
    className,
    mainTagClassName,
    searchQuery,
    setSearchQuery,
    handleSearch,
    pageTitle,
    totalElements,
    currentPage,
    totalPages,
    showUploadButton,
    role,
    onPageChange
}) => {
    return (
        <div className={`${className} min-h-screen`}>
            <Header
                searchQuery={searchQuery}
                setSearchQuery={setSearchQuery}
                handleSearch={handleSearch} 
            />
            {pageTitle && (
                <PageTitle title={pageTitle} totalElements={totalElements} />
            )}
            <main className={mainTagClassName}>
                {children}
            </main>
            {totalPages > 0 && (
                <Pagination
                    currentPage={currentPage}
                    totalPages={totalPages}
                    onPageChange={onPageChange}
                />
            )}
            <Footer />
            {showUploadButton && role === 'ROLE_MASTER' && (
                <Link
                    href={'/upload'}
                    className='fixed right-3 bottom-3
                            w-10 h-10 p-6 flex justify-center items-center
                            bg-gradient-to-t from-bgLightPurple to-bgPurple text-white text-2xl 
                            outline-none rounded-lg cursor-pointer'>
                    +
                </Link>
            )}
        </div>
    );
};

export default MainLayout;