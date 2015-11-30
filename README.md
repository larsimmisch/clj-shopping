# clj-shopping

_Clojure is hard, let's go shopping_

This is a toy project, written while I was learning Clojure.

It is a small webservice that handles updates to a single shopping list.

### To retrieve the shopping list:

```
curl "http://localhost:3000/list"
```

### To modify the shopping list:

```
curl --request PATCH "http://localhost:3000/list?action=add&shop=Supermarkt&item=Milch"
```

## License

Copyright © 2015 Lars Immisch

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
