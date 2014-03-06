package org.carlspring.strongbox.configuration;

import org.carlspring.strongbox.storage.Storage;
import org.carlspring.strongbox.storage.repository.Repository;

import java.util.*;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mtodorov
 */
public class LDAPConfigurationMappingConverter
        implements Converter
{

    private static final Logger logger = LoggerFactory.getLogger(LDAPConfigurationMappingConverter.class);


    public boolean canConvert(Class clazz)
    {
        return AbstractList.class.isAssignableFrom(clazz);
    }

    public void marshal(Object value,
                        HierarchicalStreamWriter writer,
                        MarshallingContext context)
    {
        //noinspection unchecked
        /*
        Map<String, String> map = (LinkedHashMap<String, String>) value;

        for (Map.Entry<String, Storage> entry : map.entrySet())
        {
            Storage storage = entry.getValue();

            writer.startNode("storage");
            writer.startNode("basedir");
            writer.setValue(storage.getBasedir());
            writer.endNode();

            if (storage.getName() != null)
            {
                writer.startNode("name");
                writer.setValue(storage.getName());
                writer.endNode();
            }

            for (String repositoryKey : storage.getRepositories().keySet())
            {
                Repository repository = storage.getRepositories().get(repositoryKey);
                writeRepositoryNode(writer, repository);
            }

            writer.endNode();
        }
        */
    }

    public Object unmarshal(HierarchicalStreamReader reader,
                            UnmarshallingContext context)
    {
        Map<String, String> map = new HashMap<String, String>();

        while (reader.hasMoreChildren())
        {
            reader.moveDown();

            final String nodeName = reader.getNodeName();
            if (nodeName.equals("field"))
            {
                while (reader.hasMoreChildren())
                {
                    reader.moveDown();

                    System.out.println(reader.getNodeName());

                    /*
                    if (reader.getNodeName().equals("name"))
                    {
                        storage.setName(reader.getValue().trim());
                    }
                    else if (reader.getNodeName().equals("basedir"))
                    {
                        storage.setBasedir(reader.getValue().trim());
                    }
                    else if (reader.getNodeName().equals("repositories"))
                    {
                        final Map<String, Repository> repositories = parseRepositories(reader, context);
                        storage.setRepositories(repositories);
                    }
                    else
                    {
                        logger.warn("Not parsing node " + reader.getNodeName());
                    }
                    */

                    reader.moveUp();
                }

                // map.put(storage.getName(), storage);
            }

            reader.moveUp();
        }

        return map;
    }

    private Map<String, Repository> parseRepositories(HierarchicalStreamReader reader,
                                                      UnmarshallingContext context)
    {
        Map<String, Repository> repositories = new LinkedHashMap<String, Repository>();

        while (reader.hasMoreChildren())
        {
            reader.moveDown();

            Repository repository = (Repository) context.convertAnother(null, Repository.class);
            repositories.put(repository.getName(), repository);

            reader.moveUp();
        }

        return repositories;
    }

    private void writeRepositoryNode(HierarchicalStreamWriter writer,
                                     Repository repository)
    {
        writer.startNode("repository");
        writer.addAttribute("name", repository.getName());
        writer.addAttribute("implementation", repository.getImplementation());
        writer.addAttribute("policy", repository.getPolicy());
        writer.addAttribute("type", repository.getType());
        writer.endNode();
    }

}