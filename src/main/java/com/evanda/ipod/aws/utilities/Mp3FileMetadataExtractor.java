package com.evanda.ipod.aws.utilities;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.evanda.ipod.dynamoDB.MusicInfo;
import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

/**
 * Utility class to create the MusicInfoObject for database read/write
 * 
 * 
 * @author anayak
 *
 */
public class Mp3FileMetadataExtractor {
	
	private final static SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMddHHmmss");
	
	private final static String NO_ID3_TAG_MSG = "Sorry, this MP3 file has no ID3 tag. Unable to proceed with processing";
	
	private final static String ID3_TAG_MSG = "File is marked for Processing";

	/**
	 * Reads a MP3 file from the AWS tmp directory and extracts the file's metadata. 
	 * This metadata is being used to create a MusicInfo object, that models a MusicInfo table in DynamoDB
	 * 
	 * @param pFilePath
	 * @return MusicInfo
	 * @throws UnsupportedTagException
	 * @throws InvalidDataException
	 * @throws IOException
	 */
	public static MusicInfo createMusicInfoObjectFromFile(String pFilePath) throws UnsupportedTagException, InvalidDataException, IOException{
		MusicInfo _musicInfo = null;
		
		Mp3File _mp3File = new Mp3File(pFilePath);
		if(_mp3File != null){
			
			_musicInfo = new MusicInfo(_mp3File.getFilename(), 
					_mp3File.getLengthInSeconds(), 
					_mp3File.getBitrate(), 
					_mp3File.getSampleRate(), 
					_mp3File.hasId3v1Tag(), 
					_mp3File.hasCustomTag(), 
					SDF.format(new Date()));
			
			_musicInfo.setUuid(_musicInfo.hashCode());
			
			if(!_mp3File.hasId3v1Tag()){
				_musicInfo.setSystemComments(NO_ID3_TAG_MSG);
			}else{
				_musicInfo.setSystemComments(ID3_TAG_MSG);
				ID3v1 _id3v1Tag = _mp3File.getId3v1Tag();
				_musicInfo.setAlbum(_id3v1Tag.getAlbum());
				_musicInfo.setArtist(_id3v1Tag.getArtist());
				_musicInfo.setComment(_id3v1Tag.getComment());
				_musicInfo.setTitle(_id3v1Tag.getTitle());
				_musicInfo.setTrack(_id3v1Tag.getTrack());
				_musicInfo.setYear(_id3v1Tag.getYear());
				_musicInfo.setGenre(_id3v1Tag.getGenreDescription());
				
			}
		}
		
		return _musicInfo;
	}
	
}
