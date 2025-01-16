import MainLayout from '@/layouts/MainLayout';
import React, { useState } from 'react';
import Dropzone from '@/components/templates/Dropzone';

function Convert({ supportedFormats }) {
  const [searchQuery, setSearchQuery] = useState('');


  const handleSearch = () => {
    const trimmedSearchQuery = searchQuery.trim();
    if (!trimmedSearchQuery) {
      return; // Do not proceed if the search query is empty
    }
    setSubmittedSearchQuery(trimmedSearchQuery);
    router.push(`/search?query=${encodeURIComponent(trimmedSearchQuery)}`);
  };

  return (
    <MainLayout
      pageTitle="Image Format Converter"
      searchQuery={searchQuery}
      setSearchQuery={setSearchQuery}
      handleSearch={handleSearch}
      className="app w-full flex flex-col relative"
      mainTagClassName="main w-full py-4 px-80"
    >
      <Dropzone />
    </MainLayout>
  );
}

// Fetch supported formats using getStaticProps
export async function getStaticProps() {
  // Assume supported formats are loaded from an API or another source
  const supportedFormats = ['jpg', 'png', 'gif'];

  return {
    props: {
      supportedFormats,
    },
    revalidate: 10, // The page will be revalidated every 10 seconds
  };
}

export default Convert;