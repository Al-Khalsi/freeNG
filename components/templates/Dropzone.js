import React, { useState, useEffect, useRef } from 'react'
import ReactDropzone from 'react-dropzone'
import { FiUploadCloud } from 'react-icons/fi';
import { LuFileSymlink } from 'react-icons/lu';
import { BiError } from "react-icons/bi";
import { ImSpinner3 } from "react-icons/im";
import { HiOutlineDownload } from "react-icons/hi";
import loadFfmpeg from "@/utils/load-ffmpeg";
import fileToIcon from "@/utils/file-to-icon";
import compressFileName from "@/utils/compress-file-name";
import bytesToSize from "@/utils/bytes-to-size";
import { Skeleton } from './Skeleton';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from './Select';
import { MdClose, MdDone } from 'react-icons/md';
import { Button } from './Button';
import { Badge } from './Badge'
import { FFmpeg, createFFmpeg } from '@ffmpeg/ffmpeg';

const extensions = {
  image: [
    "jpg",
    "jpeg",
    "png",
    "gif",
    "bmp",
    "webp",
    "ico",
    "tif",
    "tiff",
    "svg",
    "raw",
    "tga",
  ],
  video: [
    "mp4",
    "m4v",
    "mp4v",
    "3gp",
    "3g2",
    "avi",
    "mov",
    "wmv",
    "mkv",
    "flv",
    "ogv",
    "webm",
    "h264",
    "264",
    "hevc",
    "265",
  ],
  audio: ["mp3", "wav", "ogg", "aac", "wma", "flac", "m4a"],
};

function Dropzone() {

  const [isHover, setIsHover] = useState(false);
  const [actions, setActions] = useState([]);
  const [isReady, setIsReady] = useState(false);
  const [files, setFiles] = useState([]);
  const [isLoaded, setIsLoaded] = useState(false);
  const [isConverting, setIsConverting] = useState(false);
  const [isDone, setIsDone] = useState(false);
  const ffmpegRef = useRef(null);
  const [defaultValues, setDefaultValues] = useState("video");
  const [selcted, setSelected] = useState("...");
  const [ffmpeg, setFfmpeg] = useState(null);
  const [ffmpegStatus, setFfmpegStatus] = useState("not_loaded");

  const accepted_files = {
    "image/*": extensions.image.map(ext => `.${ext}`),
    "video/*": extensions.video.map(ext => `.${ext}`),
    "audio/*": extensions.audio.map(ext => `.${ext}`),
  };

  const getMimeType = (extension) => {
    if (mimeTypes.video.includes(extension)) return `video/${extension}`;
    if (mimeTypes.audio.includes(extension)) return `audio/${extension}`;
    if (mimeTypes.image.includes(extension)) return `image/${extension}`;
    return "application/octet-stream";
  };

  const reset = () => {
    setIsDone(false);
    setActions([]);
    setFiles([]);
    setIsReady(false);
    setIsConverting(false);
  };

  const downloadAll = () => {
    for (let action of actions) {
      !action.is_error && download(action);
    }
  };

  const download = (action) => {
    const a = document.createElement("a");
    a.style.display = "none";
    a.href = action.url;
    a.download = action.output;

    document.body.appendChild(a);
    a.click();

    // Clean up after download
    URL.revokeObjectURL(action.url);
    document.body.removeChild(a);
  };

  const convertFile = async (action) => {
    console.log("⏳ Starting conversion process...");

    console.log("current");
    if (!ffmpegRef.current) {
      console.error("⚠️ FFmpeg instance is not initialized!");
      return null;
    }

    console.log("load");
    if (ffmpegStatus !== "loaded") {
      console.error("❌ FFmpeg is NOT loaded yet!");
      return null;
    }

    console.log("action");
    const { file, to } = action;
    if (!file || !file.name) {
      console.error("❌ Invalid file object", action);
      return null;
    }

    const inputExt = file.name.split('.').pop();
    const outputExt = to;

    const inputFileName = `input.${inputExt}`;
    const outputFileName = `output.${outputExt}`;

    console.log("🚀 Before try block...");
    try {
      console.log("✅ Inside try block...");
      
      const fileData = await file.arrayBuffer();
      ffmpegRef.current.FS('writeFile', inputFileName, new Uint8Array(fileData));

      console.log(`🚀 Running FFmpeg: converting ${inputFileName} to ${outputFileName}...`);
      await ffmpegRef.current.run('-i', inputFileName, outputFileName);

      const files = ffmpegRef.current.FS('readdir', '/');
      console.log("📂 Files in FFmpeg virtual system:", files);

      if (!files.includes(outputFileName)) {
        console.error(`❌ Output file ${outputFileName} not found in FFmpeg virtual system.`);
        return null;
      }

      const data = ffmpegRef.current.FS('readFile', outputFileName);
      const mimeType = getMimeType(to);
      const url = URL.createObjectURL(new Blob([data.buffer], { type: mimeType }));

      console.log("✅ File conversion successful!", { url, output: outputFileName });
      return { url, output: outputFileName };
    } catch (error) {
      console.error("❌ Error during conversion:", error);
      return null;
    }
  };

  const convert = async () => {
    console.log("ℹ️ FFmpeg status before conversion:", ffmpegStatus);

    if (ffmpegStatus !== "loaded") {
      console.error("🚨 FFmpeg is not loaded yet, cannot convert files.");
      return;
    }

    console.log("✅ FFmpeg is ready, starting conversion...");

    let tmp_actions = actions.map((elt) => ({
      ...elt,
      isConverting: true,
    }));
    setActions(tmp_actions);
    setIsConverting(true);

    for (let action of tmp_actions) {
      try {
        console.log("Converting action:", action);
        const result = await convertFile(action);
        if (!result) {
          console.error("❌ Conversion failed!");
        } else {
          console.log("✅ Conversion successful:", result);
        }

        if (result) {
          const { url, output } = result;
          tmp_actions = tmp_actions.map((elt) =>
            elt === action
              ? {
                ...elt,
                is_converted: true,
                isConverting: false,
                url,
                output,
              }
              : elt
          );
          setActions(tmp_actions);
        }
      } catch (err) {
        console.error("Conversion error:", err);
        tmp_actions = tmp_actions.map((elt) =>
          elt === action
            ? {
              ...elt,
              is_converted: false,
              isConverting: false,
              is_error: true,
            }
            : elt
        );
        setActions(tmp_actions);
      }
    }

    setIsDone(true);
    setIsConverting(false);
  };

  const handleUpload = (data) => {
    handleExitHover();
    setFiles(data);
    const tmp = [];
    data.forEach((file) => {
      const formData = new FormData();
      tmp.push({
        file_name: file.name,
        file_size: file.size,
        from: file.name.slice(((file.name.lastIndexOf(".") - 1) >>> 0) + 2),
        to: null,
        file_type: file.type,
        file,
        is_converted: false,
        isConverting: false,
        is_error: false,
      });
    });
    setActions(tmp);
  };

  const handleHover = () => setIsHover(true);
  const handleExitHover = () => setIsHover(false);
  const updateAction = (file_name, to) => {
    setActions(
      actions.map((action) => {
        if (action.file_name === file_name) {
          return {
            ...action,
            to, // Output value based on selector selection
          };
        }
        return action;
      })
    );
  };

  const checkIsReady = () => {
    let tmp_isReady = true;
    actions.forEach((action) => {
      if (!action.to) tmp_isReady = false;
    });
    setIsReady(tmp_isReady);
  };
  const deleteAction = (action) => {
    setActions(actions.filter((elt) => elt !== action));
    setFiles(files.filter((elt) => elt.name !== action.file_name));
  };

  useEffect(() => {
    if (!actions.length) {
      setIsDone(false);
      setFiles([]);
      setIsReady(false);
      setIsConverting(false);
    } else checkIsReady();
  }, [actions]);

  const ensureFfmpegLoaded = async () => {
    let retries = 5;
    while (!ffmpegRef.current && retries > 0) {
      console.log("⏳ Waiting for FFmpeg to load...");
      await new Promise(resolve => setTimeout(resolve, 1000));
      retries--;
    }

    if (!ffmpegRef.current) {
      console.error("❌ FFmpeg failed to load after multiple attempts.");
    } else {
      console.log("✅ FFmpeg is fully loaded and ready.");
    }
  };


  useEffect(() => {
    ensureFfmpegLoaded();
  }, []);


  useEffect(() => {
    async function load() {
      console.log("🟡 FFmpeg is starting to load...");
      setFfmpegStatus("loading");

      try {
        const ff = await loadFfmpeg();
        if (!ff) throw new Error("Failed to initialize FFmpeg");

        ffmpegRef.current = ff;
        setFfmpegStatus("loaded");
        console.log("✅ FFmpeg loaded successfully!");
      } catch (error) {
        console.error("❌ Error loading FFmpeg:", error);
        setFfmpegStatus("not_loaded");
      }
    }

    load();
  }, []);

  // returns
  if (actions.length) {
    return (
      <div className="space-y-6">
        {actions.map((action, i) => (
          <div
            key={i}
            className="w-full py-4 space-y-2 lg:py-0 relative cursor-pointer rounded-xl border h-fit lg:h-20 px-4 lg:px-10 flex flex-wrap lg:flex-nowrap items-center justify-between"
          >
            {!isLoaded && (
              <Skeleton className="h-full w-full -ml-10 cursor-progress absolute rounded-xl" />
            )}
            <div className="flex gap-4 items-center">
              <span className="text-2xl text-orange-600">
                {fileToIcon(action.file_type)}
              </span>
              <div className="flex items-center gap-1 w-96">
                <span className="text-md font-medium overflow-x-hidden">
                  {compressFileName(action.file_name)}
                </span>
                <span className="text-muted-foreground text-sm">
                  ({bytesToSize(action.file_size)})
                </span>
              </div>
            </div>

            {action.is_error ? (
              <Badge variant="destructive" className="flex gap-2">
                <span>Error Converting File</span>
                <BiError />
              </Badge>
            ) : action.is_converted ? (
              <Badge variant="default" className="flex gap-2 bg-green-500">
                <span>Done</span>
              </Badge>
            ) : action.isConverting ? (
              <Badge variant="default" className="flex gap-2">
                <span>Converting</span>
                <span className="animate-spin">
                  <ImSpinner3 />
                </span>
              </Badge>
            ) : (
              <div className="text-muted-foreground text-md flex items-center gap-4">
                <span>Convert to</span>
                <Select
                  onValueChange={(value) => {
                    if (extensions.audio.includes(value)) {
                      setDefaultValues("audio");
                    } else if (extensions.video.includes(value)) {
                      setDefaultValues("video");
                    }
                    setSelected(value);
                    updateAction(action.file_name, value);
                  }}
                  value={selcted}
                >
                  <SelectTrigger className="w-32 outline-none focus:outline-none focus:ring-0 text-center text-muted-foreground bg-background text-md font-medium">
                    <SelectValue placeholder="..." />
                  </SelectTrigger>
                  <SelectContent className="h-fit">
                    {action.file_type.includes("image") && (
                      <div className="grid grid-cols-2 gap-2 w-fit">
                        {extensions.image.map((elt, i) => (
                          <div key={i} className="col-span-1 text-center">
                            <SelectItem value={elt} className="mx-auto">
                              {elt}
                            </SelectItem>
                          </div>
                        ))}
                      </div>
                    )}
                    {action.file_type.includes("video") && (
                      <Tabs defaultValue={defaultValues} className="w-full">
                        <TabsList className="w-full">
                          <TabsTrigger value="video" className="w-full">
                            Video
                          </TabsTrigger>
                          <TabsTrigger value="audio" className="w-full">
                            Audio
                          </TabsTrigger>
                        </TabsList>
                        <TabsContent value="video">
                          <div className="grid grid-cols-3 gap-2 w-fit">
                            {extensions.video.map((elt, i) => (
                              <div key={i} className="col-span-1 text-center">
                                <SelectItem value={elt} className="mx-auto">
                                  {elt}
                                </SelectItem>
                              </div>
                            ))}
                          </div>
                        </TabsContent>
                        <TabsContent value="audio">
                          <div className="grid grid-cols-3 gap-2 w-fit">
                            {extensions.audio.map((elt, i) => (
                              <div key={i} className="col-span-1 text-center">
                                <SelectItem value={elt} className="mx-auto">
                                  {elt}
                                </SelectItem>
                              </div>
                            ))}
                          </div>
                        </TabsContent>
                      </Tabs>
                    )}
                    {action.file_type.includes("audio") && (
                      <div className="grid grid-cols-2 gap-2 w-fit">
                        {extensions.audio.map((elt, i) => (
                          <div key={i} className="col-span-1 text-center">
                            <SelectItem value={elt} className="mx-auto">
                              {elt}
                            </SelectItem>
                          </div>
                        ))}
                      </div>
                    )}
                  </SelectContent>
                </Select>
              </div>
            )}

            {action.is_converted ? (
              <Button variant="outline" onClick={() => download(action)}>
                Download
              </Button>
            ) : (
              <span
                onClick={() => deleteAction(action)}
                className="cursor-pointer hover:bg-muted rounded-full h-10 w-10 flex items-center justify-center text-2xl text-foreground"
              >
                <MdClose />
              </span>
            )}
          </div>
        ))}
        <div className="flex w-full justify-end">
          {isDone ? (
            <div className="space-y-4 w-fit">
              <Button
                size="lg"
                className="rounded-xl font-semibold relative py-4 text-md flex gap-2 items-center w-full"
                onClick={downloadAll}
              >
                {actions.length > 1 ? "Download All" : "Download"}
                <HiOutlineDownload />
              </Button>
              <Button
                size="lg"
                onClick={reset}
                variant="outline"
                className="rounded-xl"
              >
                Convert Another File(s)
              </Button>
            </div>
          ) : (
            <Button
              size="lg"
              disabled={!isReady || isConverting}
              className="rounded-xl font-semibold relative py-4 text-md flex items-center w-44"
              onClick={convert}
            >
              {isConverting ? (
                <span className="animate-spin text-lg">
                  <ImSpinner3 />
                </span>
              ) : (
                <span>Convert Now</span>
              )}
            </Button>
          )}
        </div>
      </div>
    );
  }

  return (
    <ReactDropzone
      onDrop={handleUpload}
      onDragEnter={handleHover}
      onDragLeave={handleExitHover}
      accept={accepted_files}
      onDropRejected={() => {
        handleExitHover();
        toast({
          variant: "destructive",
          title: "Error uploading your file(s)",
          description: "Allowed Files: Audio, Video and Images.",
          duration: 5000,
        });
      }}
      onError={() => {
        handleExitHover();
        toast({
          variant: "destructive",
          title: "Error uploading your file(s)",
          description: "Allowed Files: Audio, Video and Images.",
          duration: 5000,
        });
      }}
    >
      {({ getRootProps, getInputProps }) => (
        <div
          {...getRootProps()}
          className=" bg-background h-72 lg:h-80 xl:h-96 rounded-3xl shadow-sm border-secondary border-2 border-dashed cursor-pointer flex items-center justify-center"
        >
          <input {...getInputProps()} />
          <div className="space-y-4 text-foreground">
            {isHover ? (
              <>
                <div className="justify-center flex text-6xl">
                  <LuFileSymlink />
                </div>
                <h3 className="text-center font-medium text-2xl">
                  Yes, right there
                </h3>
              </>
            ) : (
              <>
                <div className="justify-center flex text-6xl">
                  <FiUploadCloud />
                </div>
                <h3 className="text-center font-medium text-2xl">
                  Click, or drop your files here
                </h3>
              </>
            )}
          </div>
        </div>
      )}
    </ReactDropzone>
  )
}

export default Dropzone