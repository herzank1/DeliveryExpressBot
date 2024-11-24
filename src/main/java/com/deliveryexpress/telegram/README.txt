


El Flujo de comunicacion entre el Bot y el Usuarios esta definido de la siguiente manera

El Usuario al enviar un Mensaje,Callback,Location,File se recibe un Update (Xupdate) departe de el

el xUpdate puede provenir de un grupo o usuario sin embargo el Sender sera el Usuario y el From ID puede ser
el id del Grupo o ID del usuario.

xUpdate -> getCommand -> Response
