package org.constellation.engine.register.jooq.repository;

import java.util.ArrayList;
import java.util.List;

import org.constellation.engine.register.Property;
import org.constellation.engine.register.jooq.AbstractJooqTestTestCase;
import org.constellation.engine.register.repository.PropertyRepository;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class JooqPropertiesRepositoryTestCase extends AbstractJooqTestTestCase {

    
    @Autowired
    private PropertyRepository propertyRepository;
    
    @Test
    public void all() {
        dump(propertyRepository.findAll());
        
        List<String> keys = new ArrayList<String>();
        keys.add("test");
        
        dump(propertyRepository.findIn(keys));
        
        
    }
    
    @Test
    public void getValue() {
        String value = propertyRepository.getValue("test.notfound.property", "blurp");
        Assert.assertEquals("Default value is not matching", "blurp", value);
    }
    
    @Test
    public void save() {
        Property dto = new Property();
        dto.setKey("test");
        dto.setValue("value");
        propertyRepository.save(dto);
    }

    @Test
    public void delete() {
        Property dto = new Property();
        dto.setKey("test");
        dto.setValue("value");
        propertyRepository.delete(dto);
    }

}