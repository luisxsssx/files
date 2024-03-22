package com.backend.files.services;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class FolderService {

    // Método para obtener solo las carpetas dentro del directorio raíz
    public String[] getFoldersOnly(File directory) {
        if (directory.exists() && directory.isDirectory()) { // Verifica si el directorio existe y es un directorio
                                                             // válido
            File[] files = directory.listFiles(); // Obtiene la lista de archivos dentro del directorio
            List<String> folders = new ArrayList<>(); // Lista para almacenar nombres de carpetas
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) { // Verifica si el archivo es un directorio
                        folders.add(file.getName()); // Agrega el nombre del directorio a la lista
                    }
                }
            }
            return folders.toArray(new String[0]); // Convierte la lista de nombres de carpetas a un array de Strings y
                                                   // lo devuelve
        }
        return null; // Devuelve null si el directorio no existe o no es un directorio válido
    }

    // Método para obtener todas las subcarpetas recursivamente dentro de un
    // directorio, excluyendo las subcarpetas no deseadas
    public Map<String, String> getSubFolders(File directory) {
        Map<String, String> subfoldersMap = new HashMap<>(); // Mapa para almacenar nombres de carpetas y rutas
                                                             // completas
        // Directorios que se excluirán de la lista
        List<String> excludedFolders = new ArrayList<>();
        excludedFolders.add("folders"); // Agrega aquí cualquier carpeta que desees excluir

        getSubFoldersRecursive(directory, "", subfoldersMap, excludedFolders); // Llama al método auxiliar recursivo
        return subfoldersMap;
    }

    // Método auxiliar recursivo para obtener las subcarpetas dentro de un
    // directorio, excluyendo las subcarpetas no deseadas
    private void getSubFoldersRecursive(File directory, String parentPath, Map<String, String> subfoldersMap,
            List<String> excludedFolders) {
        if (directory.exists() && directory.isDirectory()) { // Verifica si el directorio existe y es un directorio
                                                             // válido
            File[] files = directory.listFiles(); // Obtiene la lista de archivos dentro del directorio
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory() && !excludedFolders.contains(file.getName())) { // Verifica si el archivo es
                                                                                           // un directorio y no está en
                                                                                           // la lista de exclusión
                        String folderName = file.getName();
                        String folderPath = parentPath + File.separator + folderName;
                        subfoldersMap.put(folderName, folderPath); // Agrega el nombre del directorio y su ruta completa
                                                                   // al mapa
                        getSubFoldersRecursive(file, folderPath, subfoldersMap, excludedFolders); // Llama
                                                                                                  // recursivamente al
                                                                                                  // método para
                                                                                                  // explorar las
                                                                                                  // subcarpetas
                    }
                }
            }
        }
    }

    // Método para obtener solo las carpetas dentro del directorio raíz con sus
    // rutas completas
    public Map<String, String> getFoldersOnlyWithPaths(File directory) {
        Map<String, String> foldersWithPaths = new HashMap<>();
        getFoldersOnlyWithPathsRecursive(directory, "", foldersWithPaths);
        return foldersWithPaths;
    }

    // Método auxiliar recursivo para obtener las carpetas dentro de un directorio
    // raíz con sus rutas completas
    private void getFoldersOnlyWithPathsRecursive(File directory, String parentPath,
            Map<String, String> foldersWithPaths) {
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        String folderName = file.getName();
                        String folderPath = parentPath.isEmpty() ? folderName
                                : parentPath + File.separator + folderName;
                        foldersWithPaths.put(folderName, folderPath); // Agrega el nombre del directorio y su ruta
                                                                      // completa al mapa
                        getFoldersOnlyWithPathsRecursive(file, folderPath, foldersWithPaths); // Llama recursivamente al
                                                                                              // método para explorar
                                                                                              // las subcarpetas
                    }
                }
            }
        }
    }
}
