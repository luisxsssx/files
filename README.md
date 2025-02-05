# Home Cloud
In this project, we aim to create a clone of Google Drive for personal use. This backend will be consumed by a web application and, in the future, a mobile application.

## API Operations

### Get content
#### Get all content
```http
  GET /home/all-content?type=
```
| Parameter | Type     | Description                 |
|:----------| :------- |:----------------------------|
| `type`    | `string` | **Required**. The type of content to retrieve |

#### Get folder content
```http
  GET /home/folder?path=&type=
```
| Parameter | Type     | Description                 |
|:----------| :------- |:----------------------------|
| `path`    | `string` | **Required**. Path to the folder |
| `type`    | `string` | **Optional**. The type of content to retrieve |

### Manage Paper Bin
#### Get content in paper bin
```http
  GET /home/paper-bin?path=&type=
```
| Parameter | Type     | Description                 |
|:----------| :------- |:----------------------------|
| `path`    | `string` | **Required**. Path in the bin |
| `type`    | `string` | **Optional**. The type of content to retrieve |

#### Move file to paper bin
```http
  POST /home/paper-bin/{folderName}
```
| Parameter    | Type     | Description                                           |
|:------------| :------- |:------------------------------------------------------|
| `folderName` | `string` | **Optional**. The folder containing the file         |
| `filename`   | `string` | **Required**. Name of the file to move to paper bin  |

### Delete files or folders
#### Delete a file or folder
```http
  DELETE /home/delete?path=
```
| Parameter | Type     | Description                 |
|:----------| :------- |:----------------------------|
| `path`    | `string` | **Required**. Path of the file or folder to delete |

### Upload files
```http
  POST /home/upload
```
#### Form Data
| Parameter    | Type            | Description                                                                                                     |
|:-------------|:----------------|:----------------------------------------------------------------------------------------------------------------|
| `file`       | `MultipartFile` | **Required**. The file to upload                                                                                |
| `folderName` | `string`        | **Optional**. The folder to upload the file to. If omitted or empty, the file is uploaded to the root directory |

### Rename
#### Rename file or folder
```http
  POST /home/rename
```
#### Form Data
| Parameter     | Type     | Description                                                                                    |
|:--------------|:---------|:-----------------------------------------------------------------------------------------------|
| `currentName` | `string` | **Required**. The current name of the file or folder                                           |
| `newName`     | `string` | **Required**. The new name for the file or folder                                              |
| `folderName`  | `string` | **Optional**. The folder containing the file or folder. If omitted, the root directory is used |

### Download File
#### Download a file
```http
  GET /home/download?path=&type=
```
| Parameter | Type     | Description                                                 |
|:----------|:---------|:------------------------------------------------------------|
| `path`    | `string` | **Required**. The path to the file to download              |
| `type`    | `string` | **Optional**. The type of file, if needed for content-type |

### Create folder
#### Create folder
```http
  POST /home/folder/create
```
| Parameter      | Type     | Description                                                 |
|:--------------|:---------|:------------------------------------------------------------|
| `folderName`   | `string` | **Required**. The name of the folder to create             |
| `parentFolder` | `string` | **Optional**. The parent folder. If omitted, it is created in the root directory |

### View file contents
#### View a file in a specific folder
```http
  GET /home/content/{folderName}?filename=
```
| Parameter    | Type     | Description                                |
|:------------|:---------|:-------------------------------------------|
| `folderName` | `string` | **Optional**. The folder containing the file |
| `filename`   | `string` | **Required**. Name of the file to view     |

#### View a file in the root directory
```http
  GET /home/content?filename=
```
| Parameter  | Type     | Description                                |
|:----------|:---------|:-------------------------------------------|
| `filename` | `string` | **Required**. Name of the file to view     |