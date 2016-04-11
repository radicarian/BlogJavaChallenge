package interview.blog;

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
    private HashMap<String, BlogPost> posts = new HashMap();
    private PegDownProcessor pegdown = new PegDownProcessor( Extensions.ALL );
    
    /**
     * Creates of updates a blog post, and returns its UUID.
     * @param post the blog post
     * @return the UUID
     */
    public String store( BlogPost post ){
        if( StringUtils.isBlank( post.getUuid() ) ){
            post.setUuid( UUID.randomUUID().toString() );
        } 
        else {
            posts.remove( post.getUuid() );
        }
        
        if( post.getPubDate() == 0 ){
            post.setPubDate( new Date().getTime() );
        }
        
        String pull = post.getPullQuote();
        if( !StringUtils.isBlank( pull ) ){
            pull = pegdown.markdownToHtml( pull );
            post.setPullQuoteAsHtml( pull );
        }
        
        String body = post.getBody();
        if( !StringUtils.isBlank( body ) ){
            body = pegdown.markdownToHtml( body );
            post.setBodyAsHtml( body );
        }
        
        posts.put( post.getUuid(), post );
        return post.getUuid();
    }
    
    /**
     * Deletes any blog post with the given UUID.
     * @param uuid 
     */
    public void delete( String uuid ){
        posts.remove( uuid );
    }
    
    /**
     * Retrieves a single blog post based on the UUID.
     * @param uuid the UUID of the target blog post
     * @return the blog post
     */
    public BlogPost get( String uuid ) {
        BlogPost result =  posts.get( uuid );
        if( result != null ){
            result = result.clone();
        }
        return result;
    }
    
    /**
     * Retrieves all blog posts in the database.
     * @return all blog posts in the database.
     */
    public List<BlogPost> get(){
        List<BlogPost> result = new ArrayList();
        
        Set<String> keys = posts.keySet();
        for( String key : keys ){
            BlogPost post = posts.get(key);
            if( post != null ){
                post = post.clone();
            }
            result.add(  post );
        }
        
        Collections.sort( result, new BlogPostComparator() );
        return result;
    }
}
