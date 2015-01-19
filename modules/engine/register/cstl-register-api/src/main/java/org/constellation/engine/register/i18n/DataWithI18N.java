package org.constellation.engine.register.i18n;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.constellation.configuration.CstlConfigurationRuntimeException;
import org.constellation.engine.register.jooq.tables.pojos.Data;
import org.constellation.engine.register.jooq.tables.pojos.DataI18n;

import com.google.common.collect.ImmutableMap;

/**
 * Wrap {@link Data} and {@link DataI18n}.
 * 
 * @author Olivier NOUGUIER.
 *
 */
public class DataWithI18N extends org.constellation.engine.register.jooq.tables.pojos.Data {

    private Map<String, DataI18n> dataI18ns;

    public DataWithI18N(Data data, ImmutableMap<String, DataI18n> dataI18ns) {
        copyFrom(data);
        this.dataI18ns = dataI18ns;
    }

    private void copyFrom(Data data) {
        try {
            BeanUtils.copyProperties(this, data);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new CstlConfigurationRuntimeException(e);
        }
    }

    public void setDataI18n(Map<String, DataI18n> dataI18ns) {
        this.dataI18ns = dataI18ns;
    }

    public Map<String, DataI18n> getDataI18n() {
        return dataI18ns;
    }

}
