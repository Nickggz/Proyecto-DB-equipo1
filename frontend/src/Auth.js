import React, { useState, useEffect } from 'react';

// Componente para Panel de Presidente
const PanelPresidente = ({ usuario, onVolver }) => {
  const [mesas, setMesas] = useState([]);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');

  useEffect(() => {
    cargarMesasPresidente();
  }, []);

  const cargarMesasPresidente = async () => {
    setLoading(true);
    try {
      const response = await fetch(`http://localhost:8080/api/mesas/presidente/${usuario.cedula}`);
      if (response.ok) {
        const data = await response.json();
        setMesas(data);
      } else {
        setMessage('Error al cargar las mesas');
      }
    } catch (error) {
      setMessage('Error de conexión');
    } finally {
      setLoading(false);
    }
  };

  const cerrarMesa = async (mesaId) => {
    if (!window.confirm('¿Está seguro de que desea cerrar esta mesa? Esta acción no se puede deshacer.')) {
      return;
    }

    setLoading(true);
    try {
      const response = await fetch(`http://localhost:8080/api/mesas/${mesaId}/cerrar`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          cedulaPresidente: usuario.cedula,
          motivo: 'Cierre de votación por presidente de mesa'
        })
      });

      const data = await response.json();
      
      if (response.ok) {
        setMessage(`✅ ${data.mensaje}`);
        cargarMesasPresidente(); // Recargar las mesas
      } else {
        setMessage(`❌ ${data.error}`);
      }
    } catch (error) {
      setMessage('❌ Error al cerrar la mesa');
    } finally {
      setLoading(false);
    }
  };

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
        maxWidth: '800px'
      }}>
        <h2 style={{ textAlign: 'center', color: '#333', marginBottom: '30px' }}>
          👨‍💼 Panel de Presidente de Mesa
        </h2>
        
        <div style={{
          backgroundColor: '#f8f9fa',
          padding: '15px',
          borderRadius: '8px',
          marginBottom: '30px'
        }}>
          <p style={{ margin: '0', color: '#666' }}>
            <strong>Presidente:</strong> {usuario.nombre} (CI: {usuario.cedula})
          </p>
        </div>

        {loading ? (
          <p style={{ textAlign: 'center', color: '#666' }}>Cargando mesas...</p>
        ) : (
          <div>
            <h3 style={{ color: '#333', marginBottom: '20px' }}>
              Mesas Asignadas ({mesas.length})
            </h3>
            
            {mesas.length === 0 ? (
              <p style={{ textAlign: 'center', color: '#666' }}>
                No tienes mesas asignadas
              </p>
            ) : (
              <div style={{ marginBottom: '30px' }}>
                {mesas.map(mesa => (
                  <div key={mesa.id} style={{
                    border: '1px solid #ddd',
                    borderRadius: '8px',
                    padding: '20px',
                    marginBottom: '15px',
                    backgroundColor: mesa.cerrada ? '#f8f9fa' : 'white'
                  }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                      <div>
                        <h4 style={{ margin: '0 0 10px 0', color: '#333' }}>
                          Mesa {mesa.numero}
                        </h4>
                        <p style={{ margin: '0 0 5px 0', color: '#666' }}>
                          <strong>Estado:</strong> 
                          <span style={{ 
                            color: mesa.cerrada ? '#dc3545' : '#28a745',
                            fontWeight: 'bold',
                            marginLeft: '8px'
                          }}>
                            {mesa.cerrada ? '🔒 CERRADA' : '🔓 ABIERTA'}
                          </span>
                        </p>
                        <p style={{ margin: '0 0 5px 0', color: '#666' }}>
                          <strong>Votos emitidos:</strong> {mesa.totalVotosEmitidos || 0}
                        </p>
                        {mesa.fechaCierre && (
                          <p style={{ margin: '0', color: '#666' }}>
                            <strong>Cerrada:</strong> {new Date(mesa.fechaCierre).toLocaleString()}
                          </p>
                        )}
                      </div>
                      
                      {!mesa.cerrada && (
                        <button
                          onClick={() => cerrarMesa(mesa.id)}
                          disabled={loading}
                          style={{
                            padding: '10px 20px',
                            backgroundColor: loading ? '#ccc' : '#dc3545',
                            color: 'white',
                            border: 'none',
                            borderRadius: '6px',
                            fontSize: '14px',
                            cursor: loading ? 'not-allowed' : 'pointer'
                          }}
                        >
                          {loading ? 'Cerrando...' : 'Cerrar Mesa'}
                        </button>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {message && (
          <div style={{
            padding: '12px',
            borderRadius: '6px',
            marginBottom: '20px',
            backgroundColor: message.includes('❌') ? '#fee' : '#efe',
            color: message.includes('❌') ? '#c33' : '#060',
            border: `1px solid ${message.includes('❌') ? '#fcc' : '#cfc'}`
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
          cursor: 'pointer'
        }}>
          Volver al Menú
        </button>
      </div>
    </div>
  );
};

// Componente para Ver Estado de Mesas
const EstadoMesas = ({ onVolver }) => {
  const [mesas, setMesas] = useState([]);
  const [loading, setLoading] = useState(false);
  const [filtro, setFiltro] = useState('todas'); // 'todas', 'abiertas', 'cerradas'

  useEffect(() => {
    cargarMesas();
  }, [filtro]);

  const cargarMesas = async () => {
    setLoading(true);
    try {
      let url = 'http://localhost:8080/api/mesas';
      if (filtro === 'abiertas') url += '/abiertas';
      if (filtro === 'cerradas') url += '/cerradas';

      const response = await fetch(url);
      if (response.ok) {
        const data = await response.json();
        setMesas(data);
      }
    } catch (error) {
      console.error('Error al cargar mesas:', error);
    } finally {
      setLoading(false);
    }
  };

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
        maxWidth: '900px'
      }}>
        <h2 style={{ textAlign: 'center', color: '#333', marginBottom: '30px' }}>
          📊 Estado de Mesas Electorales
        </h2>

        <div style={{ display: 'flex', gap: '10px', marginBottom: '30px', justifyContent: 'center' }}>
          {['todas', 'abiertas', 'cerradas'].map(f => (
            <button
              key={f}
              onClick={() => setFiltro(f)}
              style={{
                padding: '10px 20px',
                backgroundColor: filtro === f ? '#007bff' : '#f8f9fa',
                color: filtro === f ? 'white' : '#333',
                border: '1px solid #ddd',
                borderRadius: '6px',
                cursor: 'pointer',
                textTransform: 'capitalize'
              }}
            >
              {f}
            </button>
          ))}
        </div>

        {loading ? (
          <p style={{ textAlign: 'center', color: '#666' }}>Cargando mesas...</p>
        ) : (
          <div style={{ marginBottom: '30px' }}>
            <p style={{ textAlign: 'center', color: '#666', marginBottom: '20px' }}>
              Mostrando {mesas.length} mesas {filtro !== 'todas' ? filtro : ''}
            </p>
            
            <div style={{ display: 'grid', gap: '15px' }}>
              {mesas.map(mesa => (
                <div key={mesa.id} style={{
                  border: '1px solid #ddd',
                  borderRadius: '8px',
                  padding: '20px',
                  backgroundColor: mesa.cerrada ? '#f8f9fa' : 'white'
                }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <div>
                      <h4 style={{ margin: '0 0 10px 0', color: '#333' }}>
                        Mesa {mesa.numero}
                      </h4>
                      <p style={{ margin: '0 0 5px 0', color: '#666' }}>
                        <strong>Presidente:</strong> {mesa.presidenteNombre || 'No asignado'}
                      </p>
                      <p style={{ margin: '0 0 5px 0', color: '#666' }}>
                        <strong>Votos emitidos:</strong> {mesa.totalVotosEmitidos || 0}
                      </p>
                      {mesa.fechaCierre && (
                        <p style={{ margin: '0', color: '#666' }}>
                          <strong>Cerrada:</strong> {new Date(mesa.fechaCierre).toLocaleString()}
                        </p>
                      )}
                    </div>
                    
                    <div style={{
                      padding: '8px 16px',
                      borderRadius: '20px',
                      backgroundColor: mesa.cerrada ? '#dc3545' : '#28a745',
                      color: 'white',
                      fontSize: '14px',
                      fontWeight: 'bold'
                    }}>
                      {mesa.cerrada ? '🔒 CERRADA' : '🔓 ABIERTA'}
                    </div>
                  </div>
                </div>
              ))}
            </div>
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
          cursor: 'pointer'
        }}>
          Volver al Menú
        </button>
      </div>
    </div>
  );
};

// Componente de votación integrado (mantenido igual)
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
          mesaId: usuario?.mesaId || 1,
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
            {usuario?.circuito && (
              <><br /><span>Circuito: {usuario.circuito.nombre} ({usuario.circuito.departamento})</span></>
            )}
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
              {usuario?.circuito && (
                <><br /><span>Circuito: {usuario.circuito.nombre} ({usuario.circuito.departamento})</span></>
              )}
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
  const [formData, setFormData] = useState({
    cedula: '',
    credencialCivica: ''
  });
  const [message, setMessage] = useState('');
  const [loading, setLoading] = useState(false);
  const [usuarioLogueado, setUsuarioLogueado] = useState(null);
  const [vistaActual, setVistaActual] = useState('menu'); // 'menu', 'votacion', 'panel-presidente', 'estado-mesas'

  const API_URL = 'http://localhost:8080/api';

  // Función para obtener el emoji del rol
  const getRolEmoji = (rol) => {
    switch (rol) {
      case 'PRESIDENTE_MESA': return '👨‍💼';
      case 'SECRETARIO_MESA': return '📝';
      case 'VOCAL_MESA': return '🗣️';
      case 'ADMIN': return '👑';
      default: return '🗳️';
    }
  };

  // Función para obtener el nombre legible del rol
  const getRolNombre = (rol) => {
    switch (rol) {
      case 'PRESIDENTE_MESA': return 'Presidente de Mesa';
      case 'SECRETARIO_MESA': return 'Secretario de Mesa';
      case 'VOCAL_MESA': return 'Vocal de Mesa';
      case 'ADMIN': return 'Administrador';
      default: return 'Votante';
    }
  };

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
    
    if (numeros.length > 8) return formData.cedula;
    
    if (numeros.length <= 1) return numeros;
    if (numeros.length <= 4) return `${numeros.slice(0, 1)}.${numeros.slice(1)}`;
    if (numeros.length <= 7) return `${numeros.slice(0, 1)}.${numeros.slice(1, 4)}.${numeros.slice(4)}`;
    return `${numeros.slice(0, 1)}.${numeros.slice(1, 4)}.${numeros.slice(4, 7)}-${numeros.slice(7)}`;
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    
    if (name === 'cedula') {
      const formateada = formatearCedula(value);
      setFormData(prev => ({ ...prev, cedula: formateada }));
    } else if (name === 'credencialCivica') {
      // Formatear credencial cívica a mayúsculas
      setFormData(prev => ({ ...prev, credencialCivica: value.toUpperCase() }));
    } else {
      setFormData(prev => ({ ...prev, [name]: value }));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setMessage('');

    if (!validarCedulaUruguaya(formData.cedula)) {
      setMessage('Cédula uruguaya inválida');
      setLoading(false);
      return;
    }

    if (!formData.credencialCivica || formData.credencialCivica.trim().length < 6) {
      setMessage('Credencial cívica inválida (mínimo 6 caracteres)');
      setLoading(false);
      return;
    }

    try {
      const response = await fetch(`${API_URL}/login`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          cedula: formData.cedula.replace(/[.-]/g, ''),
          credencialCivica: formData.credencialCivica.trim()
        })
      });
      
      const result = await response.json();
      
      if (result.success) {
        setMessage(`¡Bienvenido, ${result.nombre}!`);
        
        setUsuarioLogueado({
          cedula: formData.cedula.replace(/[.-]/g, ''),
          credencialCivica: formData.credencialCivica.trim(),
          nombre: result.nombre,
          email: result.email,
          circuito: result.circuito,
          mesaId: result.mesaId,
          // NUEVOS CAMPOS DE ROL
          rol: result.rol,
          esPresidente: result.esPresidente,
          esMiembroMesa: result.esMiembroMesa,
          esAdmin: result.esAdmin
        });
      } else {
        setMessage(result.message || 'Error en el login');
      }
    } catch (error) {
      setMessage('Error de conexión. Verifica que el backend esté corriendo en el puerto 8080');
    } finally {
      setLoading(false);
    }
  };

  const cerrarSesion = () => {
    setUsuarioLogueado(null);
    setVistaActual('menu');
    setMessage('');
    setFormData({
      cedula: '',
      credencialCivica: ''
    });
  };

  // Renderizar vistas según el estado actual
  if (usuarioLogueado && vistaActual === 'votacion') {
    return <ComponenteVotacion usuario={usuarioLogueado} onVolver={() => setVistaActual('menu')} />;
  }

  if (usuarioLogueado && vistaActual === 'panel-presidente') {
    return <PanelPresidente usuario={usuarioLogueado} onVolver={() => setVistaActual('menu')} />;
  }

  if (usuarioLogueado && vistaActual === 'estado-mesas') {
    return <EstadoMesas onVolver={() => setVistaActual('menu')} />;
  }

  // Si el usuario está logueado, mostrar menú principal con opciones según rol
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
          maxWidth: '500px',
          textAlign: 'center'
        }}>
          <h2 style={{ color: '#333', marginBottom: '20px' }}>
            ¡Bienvenido, {usuarioLogueado.nombre}!
          </h2>
          
          <div style={{ 
            backgroundColor: '#f8f9fa', 
            padding: '20px', 
            borderRadius: '8px', 
            marginBottom: '30px',
            textAlign: 'left'
          }}>
            <p style={{ margin: '0 0 8px 0', color: '#666' }}>
              <strong>Cédula:</strong> {usuarioLogueado.cedula}
            </p>
            <p style={{ margin: '0 0 8px 0', color: '#666' }}>
              <strong>Credencial:</strong> {usuarioLogueado.credencialCivica}
            </p>
            <p style={{ margin: '0 0 8px 0', color: '#666', display: 'flex', alignItems: 'center' }}>
              <strong>Rol:</strong> 
              <span style={{ marginLeft: '8px' }}>
                {getRolEmoji(usuarioLogueado.rol)} {getRolNombre(usuarioLogueado.rol)}
              </span>
            </p>
            {usuarioLogueado.circuito && (
              <>
                <p style={{ margin: '0 0 8px 0', color: '#666' }}>
                  <strong>Departamento:</strong> {usuarioLogueado.circuito.departamento}
                </p>
                <p style={{ margin: '0', color: '#666' }}>
                  <strong>Circuito:</strong> {usuarioLogueado.circuito.numero} - {usuarioLogueado.circuito.nombre}
                </p>
              </>
            )}
          </div>

          <div style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
            {/* Botón de Votar - Para todos los usuarios */}
            <button 
              onClick={() => setVistaActual('votacion')}
              style={{
                width: '100%',
                padding: '14px',
                backgroundColor: '#007bff',
                color: 'white',
                border: 'none',
                borderRadius: '6px',
                fontSize: '16px',
                cursor: 'pointer',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                gap: '8px'
              }}
            >
              🗳️ Votar
            </button>

            {/* Botón Panel Presidente - Solo para presidentes */}
            {usuarioLogueado.esPresidente && (
              <button 
                onClick={() => setVistaActual('panel-presidente')}
                style={{
                  width: '100%',
                  padding: '14px',
                  backgroundColor: '#dc3545',
                  color: 'white',
                  border: 'none',
                  borderRadius: '6px',
                  fontSize: '16px',
                  cursor: 'pointer',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  gap: '8px'
                }}
              >
                👨‍💼 Panel de Presidente
              </button>
            )}

            {/* Botón Estado de Mesas - Para miembros de mesa y admins */}
            {(usuarioLogueado.esMiembroMesa || usuarioLogueado.esAdmin) && (
              <button 
                onClick={() => setVistaActual('estado-mesas')}
                style={{
                  width: '100%',
                  padding: '14px',
                  backgroundColor: '#28a745',
                  color: 'white',
                  border: 'none',
                  borderRadius: '6px',
                  fontSize: '16px',
                  cursor: 'pointer',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  gap: '8px'
                }}
              >
                📊 Estado de Mesas
              </button>
            )}

            {/* Botón Cerrar Sesión */}
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

          {/* Mostrar permisos especiales */}
          {(usuarioLogueado.esPresidente || usuarioLogueado.esMiembroMesa || usuarioLogueado.esAdmin) && (
            <div style={{
              marginTop: '20px',
              padding: '15px',
              backgroundColor: '#e3f2fd',
              borderRadius: '8px',
              border: '1px solid #bbdefb'
            }}>
              <p style={{ margin: '0', fontSize: '14px', color: '#1976d2' }}>
                <strong>🔑 Permisos especiales:</strong>
              </p>
              <ul style={{ margin: '8px 0 0 0', fontSize: '13px', color: '#424242', textAlign: 'left' }}>
                {usuarioLogueado.esPresidente && <li>• Cerrar mesas de votación</li>}
                {usuarioLogueado.esMiembroMesa && <li>• Ver estado de todas las mesas</li>}
                {usuarioLogueado.esAdmin && <li>• Administrar sistema electoral</li>}
              </ul>
            </div>
          )}
        </div>
      </div>
    );
  }

  // Formulario de login
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
            Iniciar Sesión Electoral
          </h2>
          <p style={{ color: '#666', margin: '0' }}>
            Ingresa tu cédula y credencial cívica para votar
          </p>
        </div>

        <form onSubmit={handleSubmit} className="auth-form">
          <div className="form-group" style={{ marginBottom: '20px' }}>
            <label htmlFor="cedula" style={{ 
              display: 'block', 
              marginBottom: '8px', 
              color: '#333',
              fontSize: '14px',
              fontWeight: '500'
            }}>
              Cédula de Identidad *
            </label>
            <input
              type="text"
              id="cedula"
              name="cedula"
              value={formData.cedula}
              onChange={handleChange}
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

          <div className="form-group" style={{ marginBottom: '20px' }}>
            <label htmlFor="credencialCivica" style={{ 
              display: 'block', 
              marginBottom: '8px', 
              color: '#333',
              fontSize: '14px',
              fontWeight: '500'
            }}>
              Credencial Cívica *
            </label>
            <input
              type="text"
              id="credencialCivica"
              name="credencialCivica"
              value={formData.credencialCivica}
              onChange={handleChange}
              placeholder="AAA123456"
              required
              className="form-input"
              style={{
                width: '100%',
                padding: '12px',
                border: '1px solid #ddd',
                borderRadius: '6px',
                fontSize: '16px',
                textTransform: 'uppercase'
              }}
            />
            <small className="form-hint" style={{ 
              color: '#666', 
              fontSize: '12px',
              marginTop: '4px',
              display: 'block'
            }}>
              Ingresa el número de tu credencial cívica
            </small>
          </div>

          <button 
            type="submit" 
            disabled={loading}
            className="submit-button"
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
            {loading ? 'Verificando...' : 'Iniciar Sesión'}
          </button>
        </form>

        {message && (
          <div className={`message ${message.includes('Error') || message.includes('inválida') || message.includes('Debes') ? 'error' : 'success'}`}
               style={{
                 padding: '12px',
                 borderRadius: '6px',
                 marginTop: '20px',
                 backgroundColor: message.includes('Error') || message.includes('inválida') || message.includes('Debes') ? '#fee' : '#efe',
                 color: message.includes('Error') || message.includes('inválida') || message.includes('Debes') ? '#c33' : '#060',
                 border: `1px solid ${message.includes('Error') || message.includes('inválida') || message.includes('Debes') ? '#fcc' : '#cfc'}`
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
            ℹ️ Información importante:
          </h3>
          <ul style={{ color: '#666', fontSize: '14px', lineHeight: '1.5', margin: 0 }}>
            <li>Ingresa tu cédula con formato: X.XXX.XXX-X</li>
            <li>Ingresa tu credencial cívica (ejemplo: AAA123456)</li>
            <li>El sistema determinará automáticamente tu circuito</li>
            <li>Solo podrás votar una vez por elección</li>
          </ul>
          
          <div style={{ 
            marginTop: '15px', 
            padding: '10px', 
            backgroundColor: '#e3f2fd', 
            borderRadius: '6px',
            border: '1px solid #bbdefb'
          }}>
            <strong style={{ color: '#1976d2' }}>Credenciales de prueba:</strong>
            <ul style={{ margin: '5px 0 0 0', fontSize: '13px', color: '#424242' }}>
            <li><strong>Votante:</strong> 1.234.567-8 + AAA123456</li>
            <li><strong>Presidente:</strong> 2.345.678-9 + BBB234567</li>
            <li><strong>Secretario:</strong> 3.456.789-0 + CCC345678</li>
            <li><strong>Vocal:</strong> 4.567.890-1 + DDD456789</li>
            <li><strong>Admin:</strong> 5.678.901-2 + EEE567890</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Auth;