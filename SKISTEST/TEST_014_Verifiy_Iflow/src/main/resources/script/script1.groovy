import com.sap.gateway.ip.core.customdev.util.Message
import java.util.zip.ZipInputStream
import java.util.zip.ZipEntry


Message processData(Message message) {
	def iflowId = message.getProperties().get("iflowId")
	def body = message.getBody(byte[]);
    /*To set the body, you can use the following method. Refer SCRIPT APIs document for more detail*/
    
	//Read your zip file to byte array
	def pkgBytes = body
	
	//Get the ZIP listing
	def zipEntries = extractZipEntries(pkgBytes)
	
	//do what ever you want with the entries
// 	readEntryFromZipAsString(zipEntries,zipEntries[0].getName())
	def myfileContent = readEntryFromZipAsString(pkgBytes, 'iflw')

	message.setBody(myfileContent)

    return message
}

private extractZipEntries(byte[] content) throws IOException {
    def entries = [];
    ZipInputStream zi = null;
    try {
        zi = new ZipInputStream(new ByteArrayInputStream(content));
        ZipEntry zipEntry = null;
        while ((zipEntry = zi.getNextEntry()) != null) {
            entries.add(zipEntry);
        }
    } finally {
        if (zi != null) {
            zi.close();
        }
    }
    return entries;
}


private readEntryFromZipAsString(byte[] content, def fileName){
    def tttt = ""
	StringBuilder s = new StringBuilder()
	byte[] buffer = new byte[1024]
	int read = 0
    ZipInputStream zi = null
    try {
        zi = new ZipInputStream(new ByteArrayInputStream(content))
        ZipEntry zipEntry = null
        while ((zipEntry = zi.getNextEntry()) != null) {
            // tttt = "debugLog entry class: "+zipEntry.getClass().getName() + " "+ "Entry obj" + zipEntry);
            // tttt = "debugLog entry class: " + zipEntry.getName().endsWith("iflw")
            // tttt = "debugLog";
            if (zipEntry.getName().endsWith("iflw")){
            	while ((read = zi.read(buffer, 0, 1024)) >= 0) {
		        	s.append(new String(buffer, 0, read))
		    	}
            }
        }
    } finally {
        if (zi != null) {
            zi.close()
        }
    }
    return s.toString()
    // return tttt
}