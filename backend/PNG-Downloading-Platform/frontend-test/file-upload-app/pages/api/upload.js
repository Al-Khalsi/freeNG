import formidable from 'formidable';
import fs from 'fs';

export const config = {
    api: {
        bodyParser: false,
    },
};

const uploadFile = (req, res) => {
    const form = new formidable.IncomingForm();
    form.parse(req, (err, fields, files) => {
        if (err) {
            return res.status(500).json({message: 'Error parsing the files'});
        }

        const {parentCategoryName, subCategoryNames, dominantColors, style} = fields;
        const file = files.file;

        // Here you can handle the file and other fields as needed
        // For example, save the file to a specific directory
        const dataPath = `./uploads/${file.name}`;
        fs.rename(file.path, dataPath, (err) => {
            if (err) {
                return res.status(500).json({message: 'Error saving the file'});
            }
            res.status(200).json({
                message: 'File uploaded successfully',
                data: {parentCategoryName, subCategoryNames, dominantColors, style}
            });
        });
    });
};

export default uploadFile;