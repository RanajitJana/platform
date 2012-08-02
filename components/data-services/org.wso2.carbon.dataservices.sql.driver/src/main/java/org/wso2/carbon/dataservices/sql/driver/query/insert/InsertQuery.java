/*
 *  Copyright (c) 2005-2011, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package org.wso2.carbon.dataservices.sql.driver.query.insert;

import org.wso2.carbon.dataservices.sql.driver.parser.Constants;
import org.wso2.carbon.dataservices.sql.driver.parser.ParserUtil;
import org.wso2.carbon.dataservices.sql.driver.query.Query;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public abstract class InsertQuery extends Query {

    private String targetTable;

    private Map<Integer, String> columns;

    private Map<Integer, Object> columnValues;

    private boolean isAll = false;

    public InsertQuery(Statement stmt) throws SQLException {
        super(stmt);
        this.columns = new HashMap<Integer, String>();
        this.columnValues = new HashMap<Integer, Object>();

        preprocessTokens(getProcessedTokens());

        if (this.getColumns().size() != this.getColumnValues().size()) {
            throw new SQLException("Parameter index is out of range. The column count does not " +
                    "match the value count");
        }
    }

    private void preprocessTokens(Queue<String> tokens) throws SQLException {
        if (tokens == null || tokens.isEmpty()) {
            throw new SQLException("Unable to populate attributes");
        }
        tokens.poll();
        tokens.poll();
        if (!Constants.TABLE.equalsIgnoreCase(tokens.peek())) {
            throw new SQLException("Table name is missing");
        }
        tokens.poll();
        if (!Constants.COLUMN.equalsIgnoreCase(tokens.peek()) &&
                ParserUtil.isStringLiteral(tokens.peek())) {
            this.targetTable = tokens.poll();
        } else {
            this.isAll = true;
        }
        processColumnNames(tokens, 0);
        if (!(Constants.VALUES.equalsIgnoreCase(tokens.peek()) || Constants.VALUE.equalsIgnoreCase(tokens.peek()))) {
             throw new SQLException("Syntax Error : 'VALUE'/'VALUES' is expected");
        }
        tokens.poll();
        processColumnValues(tokens, 0, false, false, true);
    }

    private void processColumnNames(Queue<String> tokens, int colCount) throws SQLException {
        if (!Constants.COLUMN.equalsIgnoreCase(tokens.peek())) {
            return;
        }
        tokens.poll();
        if (!ParserUtil.isStringLiteral(tokens.peek())) {
            throw new SQLException("Syntax Error : String literal expected");
        }
        this.getColumns().put(colCount, tokens.poll());
        if (Constants.COLUMN.equalsIgnoreCase(tokens.peek())) {
            processColumnNames(tokens, colCount + 1);
        }
    }

    private void processColumnValues(Queue<String> tokens,
                                              int valCount,
                                              boolean isParameterized,
                                              boolean isEnd, boolean isInit) throws SQLException {
        if (!isEnd) {
            if (!Constants.PARAM_VALUE.equalsIgnoreCase(tokens.peek())) {
                throw new SQLException("Syntax Error : 'PARAM_VALUE' is expected");
            }
            tokens.poll();
            if (!ParserUtil.isStringLiteral(tokens.peek())) {
                throw new SQLException("Syntax Error : String literal expected");
            }
            if ("?".equalsIgnoreCase(tokens.peek())) {
                if (isInit) {
                    isParameterized = true;
                    isInit = false;
                }
                if (!isParameterized) {
                    throw new SQLException("Both parameters and inline parameter values are not " +
                            "allowed to exist together");
                }
                isParameterized = true;
                this.getColumnValues().put(valCount, tokens.poll());
            } else if (Constants.SINGLE_QUOTATION.equalsIgnoreCase(tokens.peek())) {
                if (isInit) {
                    isInit = false;
                    isParameterized = false;
                }
                if (isParameterized) {
                    throw new SQLException("Both parameters and inline parameter values are not " +
                            "allowed to exist together");
                }
                tokens.poll();
                StringBuilder b = new StringBuilder();
                while (Constants.SINGLE_QUOTATION.equalsIgnoreCase(tokens.peek()) || tokens.isEmpty()) {
                    b.append(tokens.poll());
                }
                this.getColumnValues().put(valCount, b.toString());
                tokens.poll();
            } else if (ParserUtil.isStringLiteral(tokens.peek())) {
                if (isInit) {
                    isInit = false;
                    isParameterized = false;
                }
                if (isParameterized) {
                    throw new SQLException("Both parameters and inline parameter values are not " +
                            "allowed to exist together");
                }
                this.getColumnValues().put(valCount, tokens.poll());
            }
            if (!Constants.PARAM_VALUE.equalsIgnoreCase(tokens.peek())) {
                isEnd = true;
            } 
            processColumnValues(tokens, valCount + 1, isParameterized, isEnd, isInit);
        }
    }

    public String getTargetTable() {
        return targetTable;
    }

    public Map<Integer, String> getColumns() {
        return columns;
    }

    public Map<Integer, Object> getColumnValues() {
        return columnValues;
    }

    public boolean isAll() {
        return isAll;
        //TODO: has to retrieve the header and compare the column count vs value count
    }

}
