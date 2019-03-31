/*
 * Project Scelight
 * 
 * Copyright (c) 2013 Andras Belicza <iczaaa@gmail.com>
 * 
 * This software is the property of Andras Belicza.
 * Copying, modifying, distributing, refactoring without the author's permission
 * is prohibited and protected by Law.
 */
package hu.scelight.bean.repfolders;

import hu.scelight.service.env.Env;
import hu.scelight.service.settings.Settings;
import hu.sllauncher.bean.Bean;

import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Bean holding the replay folders.
 * 
 * @author Andras Belicza
 */
@XmlAccessorType( XmlAccessType.FIELD )
public class RepFoldersBean extends Bean {
	
	/** Current bean version. */
	public static final int BEAN_VER = 1;
	
	/**
	 * The replay folder paths will be like
	 * /home/my_username/Games/overwatch/drive_c/users/my_username/My Documents/StarCraft II/Accounts
	 *
	 * @return The list of replay folders in Lutris games folders.
	 */
	private static List< RepFolderBean > getLutrisFolders() {
		Path lutrisGamesPath = Env.OS.getDefLutrisGamesPath();
		List< RepFolderBean > repFolderBeanList = new ArrayList<>();
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(lutrisGamesPath)) {
			if (Files.isDirectory(lutrisGamesPath)) {
				for (Path lutrisGamePath: stream) {
					if (Files.isDirectory(lutrisGamePath)) {
						String lutrisGameName = lutrisGamePath.getFileName().toString();
						Path gameUsersPath = lutrisGamePath.resolve("drive_c/users");
						if (Files.isDirectory(gameUsersPath)) {
							for (Path gameUserPath: Files.newDirectoryStream(gameUsersPath)) {
								String lutrisUserName = gameUserPath.getFileName().toString();
								Path gameAccountPath = gameUserPath.resolve("My Documents/StarCraft II/Accounts");
								if (Files.isDirectory(gameAccountPath)) {
									RepFolderBean rf = new RepFolderBean();
									rf.setOrigin( RepFolderOrigin.USER );
									rf.setPath( gameAccountPath );
									rf.setRecursive( true );
									rf.setMonitored( true );
									rf.setComment( "Lutris SC2 Replay Folder (" + lutrisGameName + "/" + lutrisUserName + ")" );
									repFolderBeanList.add( rf );
								}
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			Env.LOGGER.debug( "Can't list Lutris Game folder: " + lutrisGamesPath );
			// It's fine if we can't enumerate the contents of Lutris' ~/Games folder.
		}
		return repFolderBeanList;
	}
	
	/**
	 * Factory method to return the default replay folder beans.
	 * 
	 * @return the default replay folder beans
	 */
	public static RepFoldersBean getDefaults() {
		final RepFoldersBean rfs = new RepFoldersBean();
		
		final List< RepFolderBean > repFolderBeanList = new ArrayList<>( 2 );
		
		RepFolderBean rf = new RepFolderBean();
		rf.setOrigin( RepFolderOrigin.DEF_SC2_REP_FOLDER );
		rf.setPath( Env.OS.getDefSc2ReplayPath() );
		rf.setRecursive( true );
		rf.setMonitored( true );
		rf.setComment( "SC2 Replay Folder" );
		repFolderBeanList.add( rf );
		
		rf = new RepFolderBean();
		rf.setOrigin( RepFolderOrigin.REP_BACKUP_FOLDER );
		rf.setPath( Settings.REPLAY_BACKUP_FOLDER.defaultValue );
		rf.setRecursive( true );
		rf.setComment( "Replay Backup Folder" );
		repFolderBeanList.add( rf );
		
		repFolderBeanList.addAll( getLutrisFolders() );
		
		rfs.setReplayFolderBeanList( repFolderBeanList );
		
		return rfs;
	}
	
	/** Replay folder bean list. */
	private List< RepFolderBean > replayFolderBeanList;
	
	/**
	 * Creates a new {@link RepFoldersBean}.
	 */
	public RepFoldersBean() {
		super( BEAN_VER );
	}
	
	/**
	 * Returns the first replay folder with the specified origin.
	 * 
	 * @param origin replay folder origin whose first replay folder to return
	 * @return the first replay folder with the specified origin; or <code>null</code> if no replay folder found for the specified origin
	 */
	public RepFolderBean getRepFolderForOrigin( final RepFolderOrigin origin ) {
		for ( final RepFolderBean rf : getReplayFolderBeanList() )
			if ( rf.getOrigin() == origin )
				return rf;
		
		return null;
	}
	
	/**
	 * Returns the replay folder bean list.
	 * 
	 * @return the replay folder bean list
	 */
	public List< RepFolderBean > getReplayFolderBeanList() {
		if ( replayFolderBeanList == null )
			replayFolderBeanList = new ArrayList<>( 2 );
		return replayFolderBeanList;
	}
	
	/**
	 * Sets the replay folder bean list.
	 * 
	 * @param replayFolderBeanList replay folder bean list to be set
	 */
	public void setReplayFolderBeanList( List< RepFolderBean > replayFolderBeanList ) {
		this.replayFolderBeanList = replayFolderBeanList;
	}
	
}
