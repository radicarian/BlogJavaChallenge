package interview.blog;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.pegdown.Extensions;
import org.pegdown.PegDownProcessor;

/**
 * TODO: Implement an interface to the database, capable of storing, retrieving,
 * and deleting blog posts.
 *
 * You can choose the database (SQLites, MongoDB, PostgreSQL, or whatever) and
 * the interface method (Hibernate, hand-written SQL, etc).
 */
public class BlogDB
{
    Connection database;
    PreparedStatement deleteStatement;
    PreparedStatement selectStatement;
    PreparedStatement insertStatement;
    private PegDownProcessor pegdown = new PegDownProcessor( Extensions.ALL );

    public BlogDB() {
        try {
            // load the sqlite-JDBC driver using the current class loader
  	        Class.forName("org.sqlite.JDBC");
		} catch ( Exception e ) {
		    System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		    System.exit(0);
		}
        ConnectDatabase();
        CreateDatabase();
        CreateStatements();
    }

    private void ConnectDatabase() {
		try {
		    database = DriverManager.getConnection("jdbc:sqlite:blog.db");
		} catch ( Exception e ) {
		    System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		    System.exit(0);
		}
		System.out.println("Opened database successfully");
	}

	private void CreateDatabase() {
	    try {
            Statement statement = database.createStatement();
            statement.setQueryTimeout(10);  // set timeout to 10 sec.

            //statement.executeUpdate("drop table if exists posts");
            statement.executeUpdate("create table if not exists posts (UUID string unique primary key, " +
                                    "TITLE string, DATE integer, AUTHOR string, EMAIL string, " +
                                    "QUOTE string, BODY string, QUOTE_H string, BODY_H string)");
        } catch ( Exception e ) {
		    System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		    System.exit(0);
		}
	}

	private void CreateStatements() {
        try {
            deleteStatement = database.prepareStatement(
				"delete from posts where UUID = ?");
            deleteStatement.setQueryTimeout(10);

            selectStatement = database.prepareStatement(
				"select * from posts where UUID = ?");
            selectStatement.setQueryTimeout(10);

            insertStatement = database.prepareStatement(
				//"insert into posts values (?,?,?,?,?,?,?,?,?)");
				"insert or replace into posts values (?,?,?,?,?,?,?,?,?)");
			insertStatement.setQueryTimeout(10);
        } catch ( Exception e ) {
		    System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		    System.exit(0);
		}
	}

	private BlogPost parseRow(ResultSet row) {
		BlogPost result = null;
        try {
			result = new BlogPost(row.getString(1), row.getString(2), row.getLong(3),
			                      row.getString(4), row.getString(5), row.getString(6),
			                      row.getString(7), row.getString(8), row.getString(9));
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
		return result;
	}

    /**
     * Creates of updates a blog post, and returns its UUID.
     * @param post the blog post
     * @return the UUID
     */
    public String store( BlogPost post ){
		String uuid = "";
        try {
			uuid = StringUtils.isBlank(post.getUuid()) ?
			       UUID.randomUUID().toString() :
			       post.getUuid();
			insertStatement.setString(1, uuid);
			insertStatement.setString(2, post.getTitle());
			insertStatement.setLong(3, post.getPubDate() == 0 ?
			                           new Date().getTime() :
			                           post.getPubDate());
			insertStatement.setString(4, post.getAuthor());
			insertStatement.setString(5, post.getAuthorEmail());
			String pull = post.getPullQuote();
			String body = post.getBody();
			insertStatement.setString(6, pull);
			insertStatement.setString(7, body);
			insertStatement.setString(8, !StringUtils.isBlank( pull ) ?
				                         pegdown.markdownToHtml( pull ) :
				                         "");

			insertStatement.setString(9, !StringUtils.isBlank( body ) ?
										 pegdown.markdownToHtml( body ) :
							             "");
			insertStatement.executeUpdate();
		} catch ( Exception e ) {
		    System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		    System.exit(0);
		}
		return uuid;
    }

    /**
     * Deletes any blog post with the given UUID.
     * @param uuid
     */
    public void delete( String uuid ){
		try {
			deleteStatement.setString(1, uuid);
			deleteStatement.executeUpdate();
		} catch ( Exception e ) {
		    System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		    System.exit(0);
		}
    }

    /**
     * Retrieves a single blog post based on the UUID.
     * @param uuid the UUID of the target blog post
     * @return the blog post
     */
    public BlogPost get( String uuid ) {
		BlogPost result = null;
		try {
			selectStatement.setString(1, uuid);
			ResultSet row = selectStatement.executeQuery();
			if (!row.isBeforeFirst() ) {
			    return null;
            }
            row.next();
            result = parseRow(row);
		} catch ( Exception e ) {
		    System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		    System.exit(0);
		}
		return result;
    }

    /**
     * Retrieves all blog posts in the database.
     * @return all blog posts in the database.
     */
    public List<BlogPost> get(){
	    List<BlogPost> result = new ArrayList();
		try {

			Statement statement = database.createStatement();
            statement.setQueryTimeout(10);  // set timeout to 10 sec.
            ResultSet rows = statement.executeQuery("select * from posts");
            while (rows.next()) {
				result.add(parseRow(rows));
			}

			Collections.sort( result, new BlogPostComparator() );
		} catch ( Exception e ) {
		    System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		    System.exit(0);
		}
		return result;
    }
}
