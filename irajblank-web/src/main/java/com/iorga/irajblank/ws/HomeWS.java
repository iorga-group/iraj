package com.iorga.irajblank.ws;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import com.iorga.iraj.annotation.AllowSecurityBypassedByToken;

@ApplicationScoped
@Path("/home")
public class HomeWS {
	private final Map<String, File> uploadedFiles = new HashMap<>();

	// based on http://www.mkyong.com/webservices/jax-rs/file-upload-example-in-resteasy/
	@POST
	@Path("/upload")
	@Consumes("multipart/form-data")
	@AllowSecurityBypassedByToken
	public void upload(MultipartFormDataInput input) throws IOException {
		Map<String, List<InputPart>> formDataMap = input.getFormDataMap();
		List<InputPart> inputParts = formDataMap.get("uploadedFile");

		for (InputPart inputPart : inputParts) {
			String fileName = getFileName(inputPart.getHeaders());
			InputStream inputStream = inputPart.getBody(InputStream.class, null);
			File file = File.createTempFile(HomeWS.class.getSimpleName(), ".tmp");
			IOUtils.copy(inputStream, new FileOutputStream(file));
			uploadedFiles.put(fileName, file);
		}
	}

	@GET
	@Path("/fileList")
	public Set<String> listUploadedFiles() {
		return uploadedFiles.keySet();
	}

	@POST
	@Path("/download")
	@AllowSecurityBypassedByToken
	public Response download(@FormParam("fileName") String fileName) {
		File file = uploadedFiles.get(fileName);
		return Response.ok(file).header("Content-Disposition", "attachment; filename=\""+fileName+"\"").build();
	}

	/**
	 * header sample { Content-Type=[image/png], Content-Disposition=[form-data;
	 * name="file"; filename="filename.extension"] }
	 **/
	// get uploaded filename, is there a easy way in RESTEasy?
	private String getFileName(MultivaluedMap<String, String> header) {

		String[] contentDisposition = header.getFirst("Content-Disposition").split(";");

		for (String filename : contentDisposition) {
			if ((filename.trim().startsWith("filename"))) {

				String[] name = filename.split("=");

				String finalFileName = name[1].trim().replaceAll("\"", "");
				return finalFileName;
			}
		}
		return "unknown";
	}
}
