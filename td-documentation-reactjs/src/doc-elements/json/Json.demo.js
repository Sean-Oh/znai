import React from 'react'
import Json from './Json'

const arraySimpleData = ['word', 'red', 'another']
const objectSimpleData = {'key1': 'value1 "quote" part', 'key2': 'value2'}
const objectNestedData = {
    "key1": "value1",
    "key2": {
        "key21": "value21",
        "key22": "value22",
        "key23": {
            "key231": "value231"
        }
    },
    "key3": {
        "key31": "value31",
        "key32": "value32"
    }
}

const objectNestedDataLongNames = {
    "properties": {
        "code": {
            "type": "integer",
            "format": "int32"
        },
        "message": {
            "type": "string"
        }
    }
}

const arrayOfObjectWithinObjectData = {'accounts': [{name: 'ta1', amount: 200}, {name: 'ta2', amount: 150}]}
const arrayOfObject = [{name: 'ta1', amount: 200}, {name: 'ta2', amount: 150}]

export function jsonDemo(registry) {
    registry
        .add('array of simple', <Json data={arraySimpleData} paths={['root[1]']}/>)
        .add('record', <Json data={objectSimpleData}/>)
        .add('nested record', <Json data={objectNestedData} paths={['root.key2.key22', 'root.key3.key31']}/>)
        .add('nested record with long name', <Json data={objectNestedDataLongNames}/>)
        .add('array of records within object', <Json data={arrayOfObjectWithinObjectData}/>)
        .add('array of records', <Json data={arrayOfObject}/>)
}