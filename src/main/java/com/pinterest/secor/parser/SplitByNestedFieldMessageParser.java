package com.pinterest.secor.parser;

import com.pinterest.secor.common.SecorConfig;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SplitByNestedFieldMessageParser extends SplitByFieldMessageParser {
    private static final Logger LOG = LoggerFactory.getLogger(SplitByNestedFieldMessageParser.class);
    private String[] mSplitFieldName;

    public SplitByNestedFieldMessageParser(SecorConfig config) {
        super(config);
        if (mConfig.getMessageSplitFieldName() != null &&
                !mConfig.getMessageSplitFieldName().isEmpty()) {
            final String separatorPattern = "\\.";
            mSplitFieldName = mConfig.getMessageSplitFieldName().split(separatorPattern);
        }
    }

    @Override
    protected String extractEventType(JSONObject jsonObject) {
        String fieldValue = null;
        if (mSplitFieldName != null) {
            Object finalValue = null;
            for (int i = 0; i < mSplitFieldName.length; i++) {
                if (!jsonObject.containsKey(mSplitFieldName[i])) {
                    LOG.warn("Could not find key {} in message", mConfig.getMessageSplitFieldName());
                    break;
                }
                if (i < (mSplitFieldName.length - 1)) {
                    jsonObject = (JSONObject) jsonObject.get(mSplitFieldName[i]);
                } else {
                    finalValue = jsonObject.get(mSplitFieldName[i]);
                }
            }
            fieldValue = finalValue.toString();
        } else {
            fieldValue = jsonObject.getAsString(mConfig.getMessageSplitFieldName());
            if (fieldValue == null) {
                throw new RuntimeException("Could not find key " + mConfig.getMessageSplitFieldName() + " in Json message");
            }
        }
        return fieldValue;
    }
}
