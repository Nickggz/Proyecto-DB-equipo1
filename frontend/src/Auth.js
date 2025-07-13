import React, { useState } from 'react';

// Componente de votación integrado
const ComponenteVotacion = ({ usuario, onVolver }) => {
  const [elecciones, setElecciones] = useState([]);
  const [eleccionSeleccionada, setEleccionSeleccionada] = useState(null);
  const [listas, setListas] = useState([]);
  const [listaSeleccionada, setListaSeleccionada] = useState(null);
  const [votoEnBlanco, setVotoEnBlanco] = useState(false);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');
  const [paso, setPaso] = useState(1);

  const API_URL = 'http://localhost:8080/api/voto';

  React.useEffect(() => {
    cargarElecciones();
  }, []);

  const cargarElecciones = async () => {
    try {
      const response = await fetch(`${API_URL}/elecciones`);
      const data = await response.json();
      
      if (data.success) {
        setElecciones(data.elecciones);
      } else {
        setMessage('Error al cargar elecciones');
      }
    } catch (error) {
      setMessage('Error de conexión');
    }
  };

  const seleccionarEleccion = async (eleccion) => {
    setEleccionSeleccionada(eleccion);
    setLoading(true);
    
    try {
      const response = await fetch(`${API_URL}/verificar`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          cedula: usuario?.cedula || '',
          eleccionId: eleccion?.id?.toString() || '1'
        })
      });
      
      const data = await response.json();
      
      if (data.success && !data.yaVoto) {
        const listasResponse = await fetch(`${API_URL}/listas/${eleccion?.id || 1}`);
        const listasData = await listasResponse.json();
        
        if (listasData.success) {
          setListas(listasData.listas);
          setPaso(3);
          setMessage('');
        } else {
          setMessage('Error al cargar listas');
        }
      } else {
        setMessage(data.message);
        setPaso(1);
      }
    } catch (error) {
      setMessage('Error de conexión');
    } finally {
      setLoading(false);
    }
  };

  const confirmarVoto = async () => {
    if (!votoEnBlanco && !listaSeleccionada) {
      setMessage('Selecciona una lista o marca voto en blanco');
      return;
    }

    setLoading(true);
    
    try {
      const response = await fetch(`${API_URL}/votar`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          cedula: usuario?.cedula || '',
          eleccionId: eleccionSeleccionada?.id || 1,
          mesaId: 1,
          votoEnBlanco: votoEnBlanco,
          listaId: votoEnBlanco ? null : listaSeleccionada
        })
      });
      
      const data = await response.json();
      
      if (data.success) {
        setMessage('¡Voto registrado exitosamente! Gracias por participar.');
        setTimeout(() => {
          onVolver();
        }, 3000);
      } else {
        setMessage(data.message);
      }
    } catch (error) {
      setMessage('Error al registrar voto');
    } finally {
      setLoading(false);
    }
  };

  if (paso === 1) {
    return (
      <div style={{
        minHeight: '100vh',
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: '#f5f5f5',
        padding: '20px'
      }}>
        <div style={{
          backgroundColor: 'white',
          padding: '40px',
          borderRadius: '12px',
          boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)',
          width: '100%',
          maxWidth: '600px'
        }}>
          <h2 style={{ textAlign: 'center', color: '#333', marginBottom: '30px' }}>
            Seleccionar Elección
          </h2>
          
          <p style={{ textAlign: 'center', color: '#666', marginBottom: '30px' }}>
            Votante: {usuario?.nombre || 'Usuario'} (CI: {usuario?.cedula || 'N/A'})
          </p>

          {elecciones.length > 0 ? (
            <div>
              {elecciones.map(eleccion => (
                <div key={eleccion.id} style={{
                  border: '1px solid #ddd',
                  borderRadius: '8px',
                  padding: '20px',
                  marginBottom: '15px',
                  cursor: 'pointer',
                  transition: 'all 0.3s ease'
                }} onClick={() => seleccionarEleccion(eleccion)}>
                  <h3 style={{ margin: '0 0 10px 0', color: '#333' }}>
                    {eleccion.descripcion}
                  </h3>
                  <p style={{ margin: '0', color: '#666' }}>
                    Fecha: {new Date(eleccion.fecha).toLocaleDateString()}
                  </p>
                  <p style={{ margin: '5px 0 0 0', color: '#666' }}>
                    Tipo: {eleccion.tipo}
                  </p>
                </div>
              ))}
            </div>
          ) : (
            <p style={{ textAlign: 'center', color: '#666' }}>
              No hay elecciones activas
            </p>
          )}

          {message && (
            <div style={{
              padding: '12px',
              borderRadius: '6px',
              marginTop: '20px',
              backgroundColor: message.includes('Error') ? '#fee' : '#efe',
              color: message.includes('Error') ? '#c33' : '#060',
              border: `1px solid ${message.includes('Error') ? '#fcc' : '#cfc'}`
            }}>
              {message}
            </div>
          )}

          <button onClick={onVolver} style={{
            width: '100%',
            padding: '14px',
            backgroundColor: '#6c757d',
            color: 'white',
            border: 'none',
            borderRadius: '6px',
            fontSize: '16px',
            cursor: 'pointer',
            marginTop: '20px'
          }}>
            Volver
          </button>
        </div>
      </div>
    );
  }

  if (paso === 3) {
    return (
      <div style={{
        minHeight: '100vh',
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: '#f5f5f5',
        padding: '20px'
      }}>
        <div style={{
          backgroundColor: 'white',
          padding: '40px',
          borderRadius: '12px',
          boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)',
          width: '100%',
          maxWidth: '700px'
        }}>
          <h2 style={{ textAlign: 'center', color: '#333', marginBottom: '20px' }}>
            🗳️ Votación
          </h2>
          
          <div style={{
            backgroundColor: '#f8f9fa',
            padding: '15px',
            borderRadius: '8px',
            marginBottom: '30px'
          }}>
            <h3 style={{ margin: '0 0 10px 0', color: '#333' }}>
              {eleccionSeleccionada?.descripcion}
            </h3>
            <p style={{ margin: '0', color: '#666' }}>
              Votante: {usuario?.nombre || 'Usuario'} (CI: {usuario?.cedula || 'N/A'})
            </p>
          </div>

          <h3 style={{ color: '#333', marginBottom: '20px' }}>
            Selecciona tu opción:
          </h3>

          <div style={{ marginBottom: '30px' }}>
            {listas && listas.length > 0 ? listas.map(lista => (
              <div key={lista.id} style={{
                border: `2px solid ${listaSeleccionada === lista.id ? '#007bff' : '#ddd'}`,
                borderRadius: '8px',
                padding: '15px',
                marginBottom: '10px',
                cursor: 'pointer',
                backgroundColor: listaSeleccionada === lista.id ? '#f0f8ff' : 'white',
                transition: 'all 0.3s ease'
              }} onClick={() => {
                setListaSeleccionada(lista?.id);
                setVotoEnBlanco(false);
              }}>
                <div style={{ display: 'flex', alignItems: 'center' }}>
                  <div style={{
                    width: '40px',
                    height: '40px',
                    borderRadius: '50%',
                    backgroundColor: lista?.partido?.color || '#ccc',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    color: 'white',
                    fontWeight: 'bold',
                    marginRight: '15px'
                  }}>
                    {lista?.numero || '?'}
                  </div>
                  <div>
                    <h4 style={{ margin: '0 0 5px 0', color: '#333' }}>
                      {lista?.nombre || 'Lista sin nombre'}
                    </h4>
                    <p style={{ margin: '0', color: '#666' }}>
                      {lista?.partido?.nombre || 'Partido'} ({lista?.partido?.siglas || 'N/A'})
                    </p>
                  </div>
                </div>
              </div>
            )) : (
              <p style={{ textAlign: 'center', color: '#666' }}>
                No hay listas disponibles para esta elección
              </p>
            )}

            <div style={{
              border: `2px solid ${votoEnBlanco ? '#007bff' : '#ddd'}`,
              borderRadius: '8px',
              padding: '15px',
              cursor: 'pointer',
              backgroundColor: votoEnBlanco ? '#f0f8ff' : 'white',
              transition: 'all 0.3s ease'
            }} onClick={() => {
              setVotoEnBlanco(true);
              setListaSeleccionada(null);
            }}>
              <div style={{ display: 'flex', alignItems: 'center' }}>
                <div style={{
                  width: '40px',
                  height: '40px',
                  borderRadius: '50%',
                  backgroundColor: '#999',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  color: 'white',
                  fontWeight: 'bold',
                  marginRight: '15px'
                }}>
                  ⚪
                </div>
                <div>
                  <h4 style={{ margin: '0 0 5px 0', color: '#333' }}>
                    Voto en Blanco
                  </h4>
                  <p style={{ margin: '0', color: '#666' }}>
                    No votar por ninguna lista
                  </p>
                </div>
              </div>
            </div>
          </div>

          {message && (
            <div style={{
              padding: '12px',
              borderRadius: '6px',
              marginBottom: '20px',
              backgroundColor: message.includes('Error') || message.includes('Selecciona') ? '#fee' : '#efe',
              color: message.includes('Error') || message.includes('Selecciona') ? '#c33' : '#060',
              border: `1px solid ${message.includes('Error') || message.includes('Selecciona') ? '#fcc' : '#cfc'}`
            }}>
              {message}
            </div>
          )}

          <div style={{ display: 'flex', gap: '15px' }}>
            <button onClick={() => setPaso(1)} style={{
              flex: 1,
              padding: '14px',
              backgroundColor: '#6c757d',
              color: 'white',
              border: 'none',
              borderRadius: '6px',
              fontSize: '16px',
              cursor: 'pointer'
            }}>
              Volver
            </button>

            <button 
              onClick={confirmarVoto}
              disabled={loading || (!votoEnBlanco && !listaSeleccionada)}
              style={{
                flex: 1,
                padding: '14px',
                backgroundColor: loading || (!votoEnBlanco && !listaSeleccionada) ? '#ccc' : '#28a745',
                color: 'white',
                border: 'none',
                borderRadius: '6px',
                fontSize: '16px',
                cursor: loading || (!votoEnBlanco && !listaSeleccionada) ? 'not-allowed' : 'pointer'
              }}
            >
              {loading ? 'Registrando...' : 'Confirmar Voto'}
            </button>
          </div>
        </div>
      </div>
    );
  }

  return null;
};

const Auth = () => {
  const [isLogin, setIsLogin] = useState(true);
  const [cedula, setCedula] = useState('');
  const [nombre, setNombre] = useState('');
  const [email, setEmail] = useState('');
  const [message, setMessage] = useState('');
  const [loading, setLoading] = useState(false);
  const [usuarioLogueado, setUsuarioLogueado] = useState(null);
  const [mostrarVotacion, setMostrarVotacion] = useState(false);

  const API_URL = 'http://localhost:8080/api';

  // Validación de cédula uruguaya
  const validarCedulaUruguaya = (cedula) => {
    const cedulaLimpia = cedula.replace(/[.-]/g, '');
    
    if (!/^\d{8}$/.test(cedulaLimpia)) {
      return false;
    }

    const digitos = cedulaLimpia.split('').map(Number);
    const verificador = digitos.pop();
    
    const coeficientes = [2, 9, 8, 7, 6, 3, 4];
    let suma = 0;
    
    for (let i = 0; i < 7; i++) {
      suma += digitos[i] * coeficientes[i];
    }
    
    const resto = suma % 10;
    const digitoVerificador = resto === 0 ? 0 : 10 - resto;
    
    return digitoVerificador === verificador;
  };

  // Formatear cédula con puntos y guión
  const formatearCedula = (value) => {
    const numeros = value.replace(/\D/g, '');
    
    if (numeros.length > 8) return cedula;
    
    if (numeros.length <= 1) return numeros;
    if (numeros.length <= 4) return `${numeros.slice(0, 1)}.${numeros.slice(1)}`;
    if (numeros.length <= 7) return `${numeros.slice(0, 1)}.${numeros.slice(1, 4)}.${numeros.slice(4)}`;
    return `${numeros.slice(0, 1)}.${numeros.slice(1, 4)}.${numeros.slice(4, 7)}-${numeros.slice(7)}`;
  };

  const handleCedulaChange = (e) => {
    const formateada = formatearCedula(e.target.value);
    setCedula(formateada);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setMessage('');

    if (!validarCedulaUruguaya(cedula)) {
      setMessage('Cédula uruguaya inválida');
      setLoading(false);
      return;
    }

    try {
      const endpoint = isLogin ? '/login' : '/register';
      const data = isLogin 
        ? { cedula: cedula.replace(/[.-]/g, '') }
        : { 
            cedula: cedula.replace(/[.-]/g, ''), 
            nombre: nombre.trim(),
            email: email.trim()
          };

      const response = await fetch(`${API_URL}${endpoint}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(data)
      });
      
      const result = await response.json();
      
      if (result.success) {
        if (isLogin) {
          setMessage(`¡Bienvenido, ${result.nombre}!`);
          setUsuarioLogueado({
            cedula: cedula.replace(/[.-]/g, ''),
            nombre: result.nombre,
            email: result.email
          });
        } else {
          setMessage('¡Registro exitoso! Ya puedes iniciar sesión.');
          setCedula('');
          setNombre('');
          setEmail('');
          setIsLogin(true);
        }
      } else {
        setMessage(result.message || 'Error en la operación');
      }
    } catch (error) {
      setMessage('Error de conexión. Verifica que el backend esté corriendo en el puerto 8080');
    } finally {
      setLoading(false);
    }
  };

  const manejarVotar = () => {
    setMostrarVotacion(true);
  };

  const cerrarSesion = () => {
    setUsuarioLogueado(null);
    setMostrarVotacion(false);
    setMessage('');
    setCedula('');
    setNombre('');
    setEmail('');
  };

  // Si el usuario está logueado y quiere votar, mostrar componente de votación
  if (usuarioLogueado && mostrarVotacion) {
    return <ComponenteVotacion usuario={usuarioLogueado} onVolver={() => setMostrarVotacion(false)} />;
  }

  // Si el usuario está logueado, mostrar opciones
  if (usuarioLogueado) {
    return (
      <div className="auth-container" style={{
        minHeight: '100vh',
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: '#f5f5f5',
        padding: '20px'
      }}>
        <div className="auth-card" style={{
          backgroundColor: 'white',
          padding: '40px',
          borderRadius: '12px',
          boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)',
          width: '100%',
          maxWidth: '450px',
          textAlign: 'center'
        }}>
          <h2 style={{ color: '#333', marginBottom: '20px' }}>
            ¡Bienvenido, {usuarioLogueado.nombre}!
          </h2>
          
          <p style={{ color: '#666', marginBottom: '30px' }}>
            Cédula: {usuarioLogueado.cedula}
          </p>

          <button 
            onClick={manejarVotar}
            style={{
              width: '100%',
              padding: '14px',
              backgroundColor: '#007bff',
              color: 'white',
              border: 'none',
              borderRadius: '6px',
              fontSize: '16px',
              cursor: 'pointer',
              marginBottom: '15px'
            }}
          >
            🗳️ Votar
          </button>

          <button 
            onClick={cerrarSesion}
            style={{
              width: '100%',
              padding: '14px',
              backgroundColor: '#6c757d',
              color: 'white',
              border: 'none',
              borderRadius: '6px',
              fontSize: '16px',
              cursor: 'pointer'
            }}
          >
            Cerrar Sesión
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="auth-container" style={{
      minHeight: '100vh',
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center',
      backgroundColor: '#f5f5f5',
      padding: '20px'
    }}>
      <div className="auth-card" style={{
        backgroundColor: 'white',
        padding: '40px',
        borderRadius: '12px',
        boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)',
        width: '100%',
        maxWidth: '450px'
      }}>
        <div className="auth-header" style={{ textAlign: 'center', marginBottom: '30px' }}>
          <h2 style={{ color: '#333', marginBottom: '8px' }}>
            {isLogin ? 'Iniciar Sesión' : 'Registrarse'}
          </h2>
          <p style={{ color: '#666', margin: '0' }}>
            Sistema de autenticación con cédula uruguaya
          </p>
        </div>

        <div className="auth-tabs" style={{ 
          display: 'flex', 
          marginBottom: '30px',
          borderBottom: '1px solid #eee'
        }}>
          <button 
            className={isLogin ? 'tab active' : 'tab'}
            onClick={() => {
              setIsLogin(true);
              setMessage('');
              setCedula('');
              setNombre('');
              setEmail('');
            }}
            style={{
              flex: 1,
              padding: '12px',
              border: 'none',
              backgroundColor: 'transparent',
              color: isLogin ? '#007bff' : '#666',
              borderBottom: isLogin ? '2px solid #007bff' : '2px solid transparent',
              cursor: 'pointer',
              fontSize: '16px'
            }}
          >
            Iniciar Sesión
          </button>
          <button 
            className={!isLogin ? 'tab active' : 'tab'}
            onClick={() => {
              setIsLogin(false);
              setMessage('');
              setCedula('');
              setNombre('');
              setEmail('');
            }}
            style={{
              flex: 1,
              padding: '12px',
              border: 'none',
              backgroundColor: 'transparent',
              color: !isLogin ? '#007bff' : '#666',
              borderBottom: !isLogin ? '2px solid #007bff' : '2px solid transparent',
              cursor: 'pointer',
              fontSize: '16px'
            }}
          >
            Registrarse
          </button>
        </div>

        <div onSubmit={handleSubmit} className="auth-form">
          <div className="form-group" style={{ marginBottom: '20px' }}>
            <label htmlFor="cedula" style={{ 
              display: 'block', 
              marginBottom: '8px', 
              color: '#333',
              fontSize: '14px',
              fontWeight: '500'
            }}>
              Cédula de Identidad
            </label>
            <input
              type="text"
              id="cedula"
              value={cedula}
              onChange={handleCedulaChange}
              placeholder="1.234.567-8"
              required
              className="form-input"
              style={{
                width: '100%',
                padding: '12px',
                border: '1px solid #ddd',
                borderRadius: '6px',
                fontSize: '16px'
              }}
            />
            <small className="form-hint" style={{ 
              color: '#666', 
              fontSize: '12px',
              marginTop: '4px',
              display: 'block'
            }}>
              Formato: X.XXX.XXX-X
            </small>
          </div>

          {!isLogin && (
            <>
              <div className="form-group" style={{ marginBottom: '20px' }}>
                <label htmlFor="nombre" style={{ 
                  display: 'block', 
                  marginBottom: '8px', 
                  color: '#333',
                  fontSize: '14px',
                  fontWeight: '500'
                }}>
                  Nombre Completo
                </label>
                <input
                  type="text"
                  id="nombre"
                  value={nombre}
                  onChange={(e) => setNombre(e.target.value)}
                  placeholder="Tu nombre completo"
                  required
                  className="form-input"
                  style={{
                    width: '100%',
                    padding: '12px',
                    border: '1px solid #ddd',
                    borderRadius: '6px',
                    fontSize: '16px'
                  }}
                />
              </div>

              <div className="form-group" style={{ marginBottom: '20px' }}>
                <label htmlFor="email" style={{ 
                  display: 'block', 
                  marginBottom: '8px', 
                  color: '#333',
                  fontSize: '14px',
                  fontWeight: '500'
                }}>
                  Email
                </label>
                <input
                  type="email"
                  id="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="tu@email.com"
                  required
                  className="form-input"
                  style={{
                    width: '100%',
                    padding: '12px',
                    border: '1px solid #ddd',
                    borderRadius: '6px',
                    fontSize: '16px'
                  }}
                />
              </div>
            </>
          )}

          <button 
            type="submit" 
            disabled={loading}
            className="submit-button"
            onClick={handleSubmit}
            style={{
              width: '100%',
              padding: '14px',
              backgroundColor: loading ? '#ccc' : '#007bff',
              color: 'white',
              border: 'none',
              borderRadius: '6px',
              fontSize: '16px',
              cursor: loading ? 'not-allowed' : 'pointer',
              marginTop: '20px'
            }}
          >
            {loading ? 'Procesando...' : (isLogin ? 'Iniciar Sesión' : 'Registrarse')}
          </button>
        </div>

        {message && (
          <div className={`message ${message.includes('Error') || message.includes('inválida') ? 'error' : 'success'}`}
               style={{
                 padding: '12px',
                 borderRadius: '6px',
                 marginTop: '20px',
                 backgroundColor: message.includes('Error') || message.includes('inválida') ? '#fee' : '#efe',
                 color: message.includes('Error') || message.includes('inválida') ? '#c33' : '#060',
                 border: `1px solid ${message.includes('Error') || message.includes('inválida') ? '#fcc' : '#cfc'}`
               }}>
            {message}
          </div>
        )}

        <div className="auth-info" style={{
          marginTop: '30px',
          padding: '20px',
          backgroundColor: '#f8f9fa',
          borderRadius: '8px',
          border: '1px solid #e9ecef'
        }}>
          <h3 style={{ color: '#333', marginBottom: '12px', fontSize: '16px' }}>
            Información sobre cédulas uruguayas:
          </h3>
          <ul style={{ color: '#666', fontSize: '14px', lineHeight: '1.5' }}>
            <li>Formato: X.XXX.XXX-X (8 dígitos)</li>
            <li>Incluye dígito verificador</li>
            <li>Ejemplos válidos: 1.234.567-8, 4.567.890-1</li>
          </ul>
        </div>
      </div>
    </div>
  );
};

export default Auth;