# Home Cloud
In this project, we aim to create a clone of Google Drive for personal use. This backend will be consumed by a web application and, in the future, a mobile application.

## API Operations

### Get content
#### Get all content
```http
  GET /home/folder?path=
```
| Parameter | Type     | Description                 |
|:----------| :------- |:----------------------------|
| `?path=`  | `string` | **Required**. For later use |

#### Get only folders
```http
  GET /home/folder?path=&type=folder
``````
| Parameter | Type     | Description                                                  |
|:----------| :------- |:-------------------------------------------------------------|
| `type`    | `string` | **Required**. Only if you want to specify the type of content|
#### Get only files
```http
  GET /home/folder?path=&type=files
```
| Parameter | Type     | Description                                                   |
|:----------| :------- |:--------------------------------------------------------------|
| `type`    | `string` | **Required**. Only if you want to specify the type of content |


### Delete files or folders
#### Delete a file o folder in the root directory
```http
  DELETE /home/delete?path=
``````
#### Delete a file o folder in a subdirectory:
```http
  DELETE /home/delete?path=subdirectory/file.txt
``````
| Parameter | Type     | Description                                                                                                              |
|:----------| :------- |:-------------------------------------------------------------------------------------------------------------------------|
| `path`    | `string` | **Required**. To specify where the element to be deleted is located (if it is left empty it is because it is at the root)|

### Upload files
```http
  POST /home/uploadFile
``````
#### Form Data
| Parameter    | Type            | Description                                                                                                     |
|:-------------|:----------------|:----------------------------------------------------------------------------------------------------------------|
| `file`       | `MultipartFile` | **Required**. The file to upload                                                                                |
| `folderName` | `string`        | **Optional**. The folder to upload the file to. If omitted or empty, the file is uploaded to the root directory |

 ### Rename
#### Rename file or folder
```http
  POST /home/rename
``````
#### Form Data
| Parameter     | Type     | Description                                                                                    |
|:--------------|:---------|:-----------------------------------------------------------------------------------------------|
| `currentName` | `string` | **Required**. The current name of the file or folder                                           |
| `newName`     | `string` | **Required**. The new name for the line or folder                                              |
| `folderName`  | `string` | **Optional**. The folder containing the file or folder, If omitted, the root directory is used |

### Download File
#### Download files to the root directory:
```http
  GET /home/download?path=/file.txt
``````
#### Download files in a subdirectory:
```http
  GET /home/download?path=subfolder/insidefolder/book.pdf
``````
| Parameter | Type     | Description                                                 |
|:----------|:---------|:------------------------------------------------------------|
| `path`    | `string` | **Required**. The path to the file to download              |
| `type`    | `string` | **Optional**.  The type of file, if needed for content-type |

### Create folder
#### Create folder in the root directory:
```http
  POST /home/folder/create
``````
#### Create folder in a subdirectory:
```http
  POST /home/folder/create
``````
| Parameter      | Type     | Description                                                 |
|:---------------|:---------|:------------------------------------------------------------|
| `folderName`   | `string` | **Required**. The path to the file to download              |
| `parentFolder` | `string` | **Optional**. The parent folder in which to create the new folder. If omitted, the folder is created in the root directory |