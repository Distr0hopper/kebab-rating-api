// javascript
import { useState, useEffect } from 'react'
import axios from 'axios'
import './App.css'

// Add ingredient options matching backend enum names and display labels
const INGREDIENT_OPTIONS = [
    { key: 'SALAD', label: 'Salat' },
    { key: 'TOMATO', label: 'Tomate' },
    { key: 'ONION', label: 'Zwiebel' },
    { key: 'CUCUMBER', label: 'Gurke' },
    { key: 'CHEESE', label: 'Käse' },
    { key: 'CORN', label: 'Mais' },
    { key: 'CARROTS', label: 'Karotten' },
]

// Add sauce options matching backend Sauces enum and display labels (German)
const SAUCE_OPTIONS = [
    { key: 'GARLIC', label: 'Knoblauchsauce' },
    { key: 'HOT_SAUCE', label: 'Scharfe Sauce' },
    { key: 'YOGURT_HERB', label: 'Joghurt-Kräutersauce' },
]

const API_URL = 'http://localhost:8080/api'

function App() {
    const [activeTab, setActiveTab] = useState('places')
    const [places, setPlaces] = useState([])
    const [kebabs, setKebabs] = useState([])
    const [users, setUsers] = useState([])
    const [reviews, setReviews] = useState([])
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState(null)

    // Forms State (create)
    const [newPlace, setNewPlace] = useState({
        name: '',
        address: '',
        city: '',
        priceRange: 'MEDIUM'
    })

    const [newUser, setNewUser] = useState({
        username: '',
        email: '',
        password: ''
    })

    const [newKebab, setNewKebab] = useState({
        placeId: '',
        name: '',
        description: '',
        price: '',
        breadTypeId: '',
        meatTypeId: '',
        isVegetarian: false,
        spicy: false,
        ingredients: [], // selected enum keys
        sauces: [] // selected enum keys
    })

    const [newReview, setNewReview] = useState({
        userId: '',
        kebabVariantId: '',
        rating: 5,
        title: '',
        comment: ''
    })

    const [breadTypes, setBreadTypes] = useState([])
    const [meatTypes, setMeatTypes] = useState([])

    // Edit State
    const [editingPlace, setEditingPlace] = useState(null)
    const [editingOriginalPlace, setEditingOriginalPlace] = useState(null)
    const [editingKebab, setEditingKebab] = useState(null)
    const [editingOriginalKebab, setEditingOriginalKebab] = useState(null)
    const [editingUser, setEditingUser] = useState(null)
    const [editingOriginalUser, setEditingOriginalUser] = useState(null)
    const [editingReview, setEditingReview] = useState(null)
    const [editingOriginalReview, setEditingOriginalReview] = useState(null)

    // Fetch Data
    useEffect(() => {
        fetchData()
    }, [activeTab])

    const fetchData = async () => {
        setLoading(true)
        setError(null)
        try {
            if (activeTab === 'places') {
                const res = await axios.get(`${API_URL}/places`)
                setPlaces(res.data)
            } else if (activeTab === 'kebabs') {
                const res = await axios.get(`${API_URL}/kebabs`)
                setKebabs(res.data)
                const breadRes = await axios.get(`${API_URL}/bread-types`)
                const meatRes = await axios.get(`${API_URL}/meat-types`)
                setBreadTypes(breadRes.data)
                setMeatTypes(meatRes.data)
            } else if (activeTab === 'users') {
                const res = await axios.get(`${API_URL}/users`)
                setUsers(res.data)
            } else if (activeTab === 'reviews') {
                const res = await axios.get(`${API_URL}/review`)
                setReviews(res.data)
                // additionally load users and kebabs for create form
                const [usersRes, kebabsRes] = await Promise.all([
                    axios.get(`${API_URL}/users`),
                    axios.get(`${API_URL}/kebabs`)
                ])
                setUsers(usersRes.data)
                setKebabs(kebabsRes.data)
            }
        } catch (err) {
            setError(err.response?.data?.message || err.message)
        } finally {
            setLoading(false)
        }
    }

    // Helper: build patch with only changed fields
    const buildPatch = (orig, edited) => {
        const patch = {}
        Object.keys(edited).forEach((k) => {
            const e = edited[k]
            if (orig && Object.prototype.hasOwnProperty.call(orig, k)) {
                if (e !== orig[k]) patch[k] = e
            } else {
                if (e !== '' && e !== null && e !== undefined) patch[k] = e
            }
        })
        return patch
    }

    // Create Place
    const handleCreatePlace = async (e) => {
        e.preventDefault()
        try {
            await axios.post(`${API_URL}/places`, newPlace)
            setNewPlace({ name: '', address: '', city: '', priceRange: 'MEDIUM' })
            fetchData()
            alert('Place created!')
        } catch (err) {
            alert(err.response?.data?.message || 'Error creating place')
        }
    }

    // Edit Place
    const openEditPlace = (place) => {
        setEditingOriginalPlace(place)
        setEditingPlace({
            name: place.name || '',
            address: place.address || '',
            city: place.city || '',
            priceRange: place.priceRange || 'MEDIUM'
        })
    }

    const handleEditPlaceSubmit = async (e) => {
        e.preventDefault()
        const patch = buildPatch(editingOriginalPlace, editingPlace)
        if (Object.keys(patch).length === 0) {
            alert('No changes to save.')
            return
        }
        try {
            await axios.put(`${API_URL}/places/${editingOriginalPlace.id}`, patch)
            setEditingPlace(null)
            setEditingOriginalPlace(null)
            fetchData()
            alert('Place updated!')
        } catch (err) {
            alert(err.response?.data?.message || 'Error updating place')
        }
    }

    // Create User
    const handleCreateUser = async (e) => {
        e.preventDefault()
        try {
            await axios.post(`${API_URL}/users`, newUser)
            setNewUser({ username: '', email: '', password: '' })
            fetchData()
            alert('User created!')
        } catch (err) {
            alert(err.response?.data?.message || 'Error creating user')
        }
    }

    // Edit User
    const openEditUser = (user) => {
        setEditingOriginalUser(user)
        setEditingUser({
            username: user.username || '',
            email: user.email || '',
            // password left empty for security; only send if provided
            password: ''
        })
    }

    const handleEditUserSubmit = async (e) => {
        e.preventDefault()
        const patch = buildPatch(editingOriginalUser, editingUser)
        // if password is empty string remove it
        if (patch.password === '') delete patch.password
        if (Object.keys(patch).length === 0) {
            alert('No changes to save.')
            return
        }
        try {
            await axios.put(`${API_URL}/users/${editingOriginalUser.id}`, patch)
            setEditingUser(null)
            setEditingOriginalUser(null)
            fetchData()
            alert('User updated!')
        } catch (err) {
            alert(err.response?.data?.message || 'Error updating user')
        }
    }

    // Create Kebab
    const handleCreateKebab = async (e) => {
        e.preventDefault()
        try {
            await axios.post(`${API_URL}/kebabs`, {
                ...newKebab,
                price: parseFloat(newKebab.price),
                // ingredients already included as enum keys
            })
            setNewKebab({
                placeId: '',
                name: '',
                description: '',
                price: '',
                breadTypeId: '',
                meatTypeId: '',
                isVegetarian: false,
                spicy: false,
                ingredients: [],
                sauces: []
            })
            fetchData()
            alert('Kebab created!')
        } catch (err) {
            alert(err.response?.data?.message || 'Error creating kebab')
        }
    }

    // Edit Kebab
    const openEditKebab = (kebab) => {
        setEditingOriginalKebab(kebab)
        // kebab.ingredients and sauces from API are display labels; map to enum keys for selection
        const selectedIngredientKeys = INGREDIENT_OPTIONS
            .filter(opt => (kebab.ingredients || []).includes(opt.label))
            .map(opt => opt.key)
        const selectedSauceKeys = SAUCE_OPTIONS
            .filter(opt => (kebab.sauces || []).includes(opt.label))
            .map(opt => opt.key)

        setEditingKebab({
            placeId: kebab.placeId || kebab.placeId || '',
            name: kebab.name || '',
            description: kebab.description || '',
            price: kebab.price != null ? String(kebab.price) : '',
            breadTypeId: kebab.breadTypeId || '',
            meatTypeId: kebab.meatTypeId || '',
            isVegetarian: !!kebab.isVegetarian,
            spicy: !!kebab.spicy,
            ingredients: selectedIngredientKeys,
            sauces: selectedSauceKeys
        })
    }

    const handleEditKebabSubmit = async (e) => {
        e.preventDefault()
        const edited = { ...editingKebab }
        if (edited.price !== '') edited.price = parseFloat(edited.price)
        const patch = buildPatch(editingOriginalKebab, edited)
        if (Object.keys(patch).length === 0) {
            alert('No changes to save.')
            return
        }
        try {
            await axios.put(`${API_URL}/kebabs/${editingOriginalKebab.id}`, patch)
            setEditingKebab(null)
            setEditingOriginalKebab(null)
            fetchData()
            alert('Kebab updated!')
        } catch (err) {
            alert(err.response?.data?.message || 'Error updating kebab')
        }
    }

    // Create Review
    const handleCreateReview = async (e) => {
        e.preventDefault()
        if (!newReview.userId || !newReview.kebabVariantId) {
            alert('Please select user and kebab variant')
            return
        }
        try {
            await axios.post(`${API_URL}/review`, {
                kebabVariantId: newReview.kebabVariantId,
                rating: Number(newReview.rating),
                title: newReview.title,
                comment: newReview.comment
            }, {
                params: { userId: newReview.userId }
            })
            setNewReview({ userId: '', kebabVariantId: '', rating: 5, title: '', comment: '' })
            fetchData()
            alert('Review created!')
        } catch (err) {
            alert(err.response?.data?.message || 'Error creating review')
        }
    }

    // Edit Review
    const openEditReview = (review) => {
        setEditingOriginalReview(review)
        setEditingReview({
            title: review.title || '',
            rating: review.rating || '',
            comment: review.comment || ''
        })
    }

    const handleEditReviewSubmit = async (e) => {
        e.preventDefault()
        const patch = buildPatch(editingOriginalReview, editingReview)
        if (Object.keys(patch).length === 0) {
            alert('No changes to save.')
            return
        }
        try {
            await axios.put(`${API_URL}/review/${editingOriginalReview.id}`, patch)
            setEditingReview(null)
            setEditingOriginalReview(null)
            fetchData()
            alert('Review updated!')
        } catch (err) {
            alert(err.response?.data?.message || 'Error updating review')
        }
    }

    // Delete
    const handleDelete = async (type, id) => {
        if (!confirm('Are you sure?')) return
        try {
            await axios.delete(`${API_URL}/${type}/${id}`)
            fetchData()
            alert('Deleted!')
        } catch (err) {
            alert(err.response?.data?.message || 'Error deleting')
        }
    }

    return (
        <div className="App">
            <h1>🌯 Kebab Rating API - Admin Panel</h1>

            <div className="tabs">
                <button onClick={() => setActiveTab('places')} className={activeTab === 'places' ? 'active' : ''}>
                    Places
                </button>
                <button onClick={() => setActiveTab('kebabs')} className={activeTab === 'kebabs' ? 'active' : ''}>
                    Kebabs
                </button>
                <button onClick={() => setActiveTab('users')} className={activeTab === 'users' ? 'active' : ''}>
                    Users
                </button>
                <button onClick={() => setActiveTab('reviews')} className={activeTab === 'reviews' ? 'active' : ''}>
                    Reviews
                </button>
            </div>

            {error && <div className="error">Error: {error}</div>}
            {loading && <div className="loading">Loading...</div>}

            {/* Places Tab */}
            {activeTab === 'places' && (
                <div className="section">
                    <h2>Create Place</h2>
                    <form onSubmit={handleCreatePlace}>
                        <input
                            placeholder="Name"
                            value={newPlace.name}
                            onChange={(e) => setNewPlace({ ...newPlace, name: e.target.value })}
                            required
                        />
                        <input
                            placeholder="Address"
                            value={newPlace.address}
                            onChange={(e) => setNewPlace({ ...newPlace, address: e.target.value })}
                            required
                        />
                        <input
                            placeholder="City"
                            value={newPlace.city}
                            onChange={(e) => setNewPlace({ ...newPlace, city: e.target.value })}
                            required
                        />
                        <select
                            value={newPlace.priceRange}
                            onChange={(e) => setNewPlace({ ...newPlace, priceRange: e.target.value })}
                        >
                            <option value="CHEAP">Cheap (€)</option>
                            <option value="MEDIUM">Medium (€€)</option>
                            <option value="EXPENSIVE">Expensive (€€€)</option>
                        </select>
                        <button type="submit">Create Place</button>
                    </form>

                    {editingPlace && (
                        <div className="edit-section">
                            <h3>Edit Place</h3>
                            <form onSubmit={handleEditPlaceSubmit}>
                                <input placeholder="Name" value={editingPlace.name} onChange={(e) => setEditingPlace({ ...editingPlace, name: e.target.value })} required />
                                <input placeholder="Address" value={editingPlace.address} onChange={(e) => setEditingPlace({ ...editingPlace, address: e.target.value })} required />
                                <input placeholder="City" value={editingPlace.city} onChange={(e) => setEditingPlace({ ...editingPlace, city: e.target.value })} required />
                                <select value={editingPlace.priceRange} onChange={(e) => setEditingPlace({ ...editingPlace, priceRange: e.target.value })}>
                                    <option value="CHEAP">Cheap (€)</option>
                                    <option value="MEDIUM">Medium (€€)</option>
                                    <option value="EXPENSIVE">Expensive (€€€)</option>
                                </select>
                                <button type="submit">Save</button>
                                <button type="button" onClick={() => { setEditingPlace(null); setEditingOriginalPlace(null) }}>Cancel</button>
                            </form>
                        </div>
                    )}

                    <h2>All Places ({places.length})</h2>
                    <div className="grid">
                        {places.map((place) => (
                            <div key={place.id} className="card">
                                <h3>{place.name}</h3>
                                <p>📍 {place.address}, {place.city}</p>
                                <p>💰 {place.priceRange}</p>
                                <p>⭐ {place.averageRating?.toFixed?.(1) || '0.0'} ({place.reviewCount || 0} reviews)</p>
                                <button onClick={() => handleDelete('places', place.id)} className="delete">Delete</button>
                                <button onClick={() => openEditPlace(place)} className="edit">Edit</button>
                            </div>
                        ))}
                    </div>
                </div>
            )}

            {/* Kebabs Tab */}
            {activeTab === 'kebabs' && (
                <div className="section">
                    <h2>Create Kebab</h2>
                    <form onSubmit={handleCreateKebab}>
                        <select
                            value={newKebab.placeId}
                            onChange={(e) => setNewKebab({ ...newKebab, placeId: e.target.value })}
                            required
                        >
                            <option value="">Select Place</option>
                            {places.map((p) => (
                                <option key={p.id} value={p.id}>{p.name}</option>
                            ))}
                        </select>
                        <input
                            placeholder="Kebab Name"
                            value={newKebab.name}
                            onChange={(e) => setNewKebab({ ...newKebab, name: e.target.value })}
                            required
                        />
                        <input
                            placeholder="Description"
                            value={newKebab.description}
                            onChange={(e) => setNewKebab({ ...newKebab, description: e.target.value })}
                        />
                        <input
                            type="number"
                            step="0.01"
                            placeholder="Price"
                            value={newKebab.price}
                            onChange={(e) => setNewKebab({ ...newKebab, price: e.target.value })}
                            required
                        />
                        <select
                            value={newKebab.breadTypeId}
                            onChange={(e) => setNewKebab({ ...newKebab, breadTypeId: e.target.value })}
                            required
                        >
                            <option value="">Select Bread Type</option>
                            {breadTypes.map((b) => (
                                <option key={b.id} value={b.id}>{b.name}</option>
                            ))}
                        </select>
                        <select
                            value={newKebab.meatTypeId}
                            onChange={(e) => setNewKebab({ ...newKebab, meatTypeId: e.target.value })}
                            required
                        >
                            <option value="">Select Meat Type</option>
                            {meatTypes.map((m) => (
                                <option key={m.id} value={m.id}>{m.name}</option>
                            ))}
                        </select>
                        <label style={{ color: 'black'}}>
                            <input
                                type="checkbox"
                                checked={newKebab.isVegetarian}
                                onChange={(e) => setNewKebab({ ...newKebab, isVegetarian: e.target.checked })}
                            />
                            Vegetarian
                        </label>
                        <label style={{color: 'black'}}>
                            <input
                                type="checkbox"
                                checked={newKebab.spicy}
                                onChange={(e) => setNewKebab({ ...newKebab, spicy: e.target.checked })}
                            />
                            Spicy
                        </label>

                        {/* Ingredients multi-select */}
                        <div className="checkbox-group">
                            <div><strong>Ingredients:</strong></div>
                            {INGREDIENT_OPTIONS.map(opt => (
                                <label key={opt.key} style={{ color: 'black', marginRight: 12 }}>
                                    <input
                                        type="checkbox"
                                        checked={newKebab.ingredients.includes(opt.key)}
                                        onChange={(e) => {
                                            const isChecked = e.target.checked
                                            setNewKebab(prev => {
                                                const set = new Set(prev.ingredients)
                                                if (isChecked) set.add(opt.key); else set.delete(opt.key)
                                                return { ...prev, ingredients: Array.from(set) }
                                            })
                                        }}
                                    /> {opt.label}
                                </label>
                            ))}
                        </div>

                        {/* Sauces multi-select */}
                        <div className="checkbox-group">
                            <div><strong>Sauces:</strong></div>
                            {SAUCE_OPTIONS.map(opt => (
                                <label key={opt.key} style={{ color: 'black', marginRight: 12 }}>
                                    <input
                                        type="checkbox"
                                        checked={newKebab.sauces.includes(opt.key)}
                                        onChange={(e) => {
                                            const isChecked = e.target.checked
                                            setNewKebab(prev => {
                                                const set = new Set(prev.sauces)
                                                if (isChecked) set.add(opt.key); else set.delete(opt.key)
                                                return { ...prev, sauces: Array.from(set) }
                                            })
                                        }}
                                    /> {opt.label}
                                </label>
                            ))}
                        </div>

                        <button type="submit">Create Kebab</button>
                    </form>

                    {editingKebab && (
                        <div className="edit-section">
                            <h3>Edit Kebab</h3>
                            <form onSubmit={handleEditKebabSubmit}>
                                <select value={editingKebab.placeId} onChange={(e) => setEditingKebab({ ...editingKebab, placeId: e.target.value })} required>
                                    <option value="">Select Place</option>
                                    {places.map((p) => (
                                        <option key={p.id} value={p.id}>{p.name}</option>
                                    ))}
                                </select>
                                <input placeholder="Kebab Name" value={editingKebab.name} onChange={(e) => setEditingKebab({ ...editingKebab, name: e.target.value })} required />
                                <input placeholder="Description" value={editingKebab.description} onChange={(e) => setEditingKebab({ ...editingKebab, description: e.target.value })} />
                                <input type="number" step="0.01" placeholder="Price" value={editingKebab.price} onChange={(e) => setEditingKebab({ ...editingKebab, price: e.target.value })} required />
                                <select value={editingKebab.breadTypeId} onChange={(e) => setEditingKebab({ ...editingKebab, breadTypeId: e.target.value })} required>
                                    <option value="">Select Bread Type</option>
                                    {breadTypes.map((b) => (
                                        <option key={b.id} value={b.id}>{b.name}</option>
                                    ))}
                                </select>
                                <select value={editingKebab.meatTypeId} onChange={(e) => setEditingKebab({ ...editingKebab, meatTypeId: e.target.value })} required>
                                    <option value="">Select Meat Type</option>
                                    {meatTypes.map((m) => (
                                        <option key={m.id} value={m.id}>{m.name}</option>
                                    ))}
                                </select>
                                <label>
                                    <input type="checkbox" checked={editingKebab.isVegetarian} onChange={(e) => setEditingKebab({ ...editingKebab, isVegetarian: e.target.checked })} />
                                    Vegetarian
                                </label>
                                <label>
                                    <input type="checkbox" checked={editingKebab.spicy} onChange={(e) => setEditingKebab({ ...editingKebab, spicy: e.target.checked })} />
                                    Spicy
                                </label>

                                {/* Ingredients multi-select for edit */}
                                <div className="checkbox-group">
                                    <div><strong>Ingredients:</strong></div>
                                    {INGREDIENT_OPTIONS.map(opt => (
                                        <label key={opt.key} style={{ color: 'black', marginRight: 12 }}>
                                            <input
                                                type="checkbox"
                                                checked={editingKebab.ingredients?.includes(opt.key) || false}
                                                onChange={(e) => {
                                                    const isChecked = e.target.checked
                                                    setEditingKebab(prev => {
                                                        const set = new Set(prev.ingredients || [])
                                                        if (isChecked) set.add(opt.key); else set.delete(opt.key)
                                                        return { ...prev, ingredients: Array.from(set) }
                                                    })
                                                }}
                                            /> {opt.label}
                                        </label>
                                    ))}
                                </div>

                                {/* Sauces multi-select for edit */}
                                <div className="checkbox-group">
                                    <div><strong>Sauces:</strong></div>
                                    {SAUCE_OPTIONS.map(opt => (
                                        <label key={opt.key} style={{ color: 'black', marginRight: 12 }}>
                                            <input
                                                type="checkbox"
                                                checked={editingKebab.sauces?.includes(opt.key) || false}
                                                onChange={(e) => {
                                                    const isChecked = e.target.checked
                                                    setEditingKebab(prev => {
                                                        const set = new Set(prev.sauces || [])
                                                        if (isChecked) set.add(opt.key); else set.delete(opt.key)
                                                        return { ...prev, sauces: Array.from(set) }
                                                    })
                                                }}
                                            /> {opt.label}
                                        </label>
                                    ))}
                                </div>

                                <button type="submit">Save</button>
                                <button type="button" onClick={() => { setEditingKebab(null); setEditingOriginalKebab(null) }}>Cancel</button>
                            </form>
                        </div>
                    )}

                    <h2>All Kebabs ({kebabs.length})</h2>
                    <div className="grid">
                        {kebabs.map((kebab) => (
                            <div key={kebab.id} className="card">
                                <h3>{kebab.name}</h3>
                                <p>🏪 {kebab.placeName}</p>
                                <p>📍 {kebab.placeCity}</p>
                                <p>💰 €{kebab.price}</p>
                                <p>🍞 {kebab.breadTypeName} | 🥩 {kebab.meatTypeName}</p>
                                <p>
                                    {kebab.isVegetarian && '🌱 '}
                                    {kebab.spicy && '🌶️ '}
                                    ⭐ {kebab.averageRating?.toFixed?.(1) || '0.0'}
                                </p>
                                <button onClick={() => handleDelete('kebabs', kebab.id)} className="delete">Delete</button>
                                <button onClick={() => openEditKebab(kebab)} className="edit">Edit</button>
                            </div>
                        ))}
                    </div>
                </div>
            )}

            {/* Users Tab */}
            {activeTab === 'users' && (
                <div className="section">
                    <h2>Create User</h2>
                    <form onSubmit={handleCreateUser}>
                        <input
                            placeholder="Username"
                            value={newUser.username}
                            onChange={(e) => setNewUser({ ...newUser, username: e.target.value })}
                            required
                        />
                        <input
                            type="email"
                            placeholder="Email"
                            value={newUser.email}
                            onChange={(e) => setNewUser({ ...newUser, email: e.target.value })}
                            required
                        />
                        <input
                            type="password"
                            placeholder="Password"
                            value={newUser.password}
                            onChange={(e) => setNewUser({ ...newUser, password: e.target.value })}
                            required
                        />
                        <button type="submit">Create User</button>
                    </form>

                    {editingUser && (
                        <div className="edit-section">
                            <h3>Edit User</h3>
                            <form onSubmit={handleEditUserSubmit}>
                                <input placeholder="Username" value={editingUser.username} onChange={(e) => setEditingUser({ ...editingUser, username: e.target.value })} required />
                                <input type="email" placeholder="Email" value={editingUser.email} onChange={(e) => setEditingUser({ ...editingUser, email: e.target.value })} required />
                                <input type="password" placeholder="New password (leave empty to keep)" value={editingUser.password} onChange={(e) => setEditingUser({ ...editingUser, password: e.target.value })} />
                                <button type="submit">Save</button>
                                <button type="button" onClick={() => { setEditingUser(null); setEditingOriginalUser(null) }}>Cancel</button>
                            </form>
                        </div>
                    )}

                    <h2>All Users ({users.length})</h2>
                    <div className="grid">
                        {users.map((user) => (
                            <div key={user.id} className="card">
                                <h3>{user.username}</h3>
                                <p>📧 {user.email}</p>
                                <p>🗓️ {new Date(user.createdAt).toLocaleDateString()}</p>
                                <button onClick={() => handleDelete('users', user.id)} className="delete">Delete</button>
                                <button onClick={() => openEditUser(user)} className="edit">Edit</button>
                            </div>
                        ))}
                    </div>
                </div>
            )}

            {/* Reviews Tab */}
            {activeTab === 'reviews' && (
                <div className="section">
                    <h2>All Reviews ({reviews.length})</h2>

                    {/* Create Review */}
                    <div className="create-section">
                        <h3>Create Review</h3>
                        <form onSubmit={handleCreateReview}>
                            <select value={newReview.userId} onChange={(e) => setNewReview({ ...newReview, userId: e.target.value })} required>
                                <option value="">Select User</option>
                                {users.map(u => (
                                    <option key={u.id} value={u.id}>{u.username} ({u.email})</option>
                                ))}
                            </select>

                            <select value={newReview.kebabVariantId} onChange={(e) => setNewReview({ ...newReview, kebabVariantId: e.target.value })} required>
                                <option value="">Select Kebab</option>
                                {kebabs.map(k => (
                                    <option key={k.id} value={k.id}>{k.name} @ {k.placeName}</option>
                                ))}
                            </select>

                            <input
                                placeholder="Title"
                                value={newReview.title}
                                onChange={(e) => setNewReview({ ...newReview, title: e.target.value })}
                                required
                            />
                            <input
                                type="number"
                                min="1"
                                max="5"
                                placeholder="Rating (1-5)"
                                value={newReview.rating}
                                onChange={(e) => setNewReview({ ...newReview, rating: e.target.value })}
                                required
                            />
                            <input
                                placeholder="Comment"
                                value={newReview.comment}
                                onChange={(e) => setNewReview({ ...newReview, comment: e.target.value })}
                                required
                            />

                            <button type="submit">Create Review</button>
                        </form>
                    </div>

                    {editingReview && (
                        <div className="edit-section">
                            <h3>Edit Review</h3>
                            <form onSubmit={handleEditReviewSubmit}>
                                <input placeholder="Title" value={editingReview.title} onChange={(e) => setEditingReview({ ...editingReview, title: e.target.value })} required />
                                <input type="number" min="1" max="5" placeholder="Rating" value={editingReview.rating} onChange={(e) => setEditingReview({ ...editingReview, rating: Number(e.target.value) })} required />
                                <input placeholder="Comment" value={editingReview.comment} onChange={(e) => setEditingReview({ ...editingReview, comment: e.target.value })} />
                                <button type="submit">Save</button>
                                <button type="button" onClick={() => { setEditingReview(null); setEditingOriginalReview(null) }}>Cancel</button>
                            </form>
                        </div>
                    )}

                    <div className="grid">
                        {reviews.map((review) => (
                            <div key={review.id} className="card">
                                <h3>{review.title}</h3>
                                <p>👤 {review.username}</p>
                                <p>🌯 {review.kebabVariantName} @ {review.placeName}</p>
                                <p>⭐ {review.rating}/5</p>
                                <p>💬 {review.comment}</p>
                                <p>🗓️ {new Date(review.createdAt).toLocaleDateString()}</p>
                                <button onClick={() => handleDelete('review', review.id)} className="delete">Delete</button>
                                <button onClick={() => openEditReview(review)} className="edit">Edit</button>
                            </div>
                        ))}
                    </div>
                </div>
            )}
        </div>
    )
}

export default App
