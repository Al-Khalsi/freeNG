import React from 'react'
import ReactDropzone from 'react-dropzone'

function Dropzone() {
  return (
    <ReactDropzone
        onDrop={handleUpload}
        onDragEnter={handleHover}
        onDragLeave={handleExitHover}
        accept={accepted_files}
        onDropRejected={''}
        onError={() => {
            handleExitHover();
            toast({
                variant: 'destructive',
                title: 'Error uploading your file',
                description: 'Allowed Files: Audio, Video and Images.',
                duration: 5000,
            });
        }}
    >

    </ReactDropzone>
  )
}

export default Dropzone