"use client";

import React, { useState, useEffect, use, useCallback } from "react";
import { AppLayout } from "@/components/layout";
import { SVG } from "@/components";
import { PersonCliService } from "@/services/client/person.cli.service";
import { Person } from "@/types";
import { useRouter } from "next/navigation";
import { Dialog } from "@/components/ui/dialog";
import { doNothing } from "@/lib";
import { useSession } from "@/providers";
import { Role } from "@/enums";

export default function PersonDetailPage({ params }: { params: Promise<{ id: string }> }) {
  const router = useRouter();
  const paramId = use(params).id;
  const id = parseInt(paramId);
  const session = useSession();

  if (isNaN(id)) {
    router.push("/dashboard");
    return null;
  }

  const [person, setPerson] = useState<Person | null>(null);
  const [loading, setLoading] = useState(true);
  const [showEditModal, setShowEditModal] = useState(false);

  const showStatusBadge = useCallback(() => {

    if (!person) return null;

    return (
        <div>
          <h1 className="text-3xl font-sans text-gray-800 mb-1">{person.name}</h1>
          <p className="text-xl text-medium-grey font-light">{person.phoneNumber}</p>
          <div className="mt-4 flex gap-2">
            {person.status !== 'DISABLED' ? null :
                <span className="mt-2 inline-block px-2 py-1 bg-gray-100 text-gray-500 text-xs rounded">
                       Inactive
                    </span>
            }
            {person.status !== 'ENABLED' ? null :
                <span className="mt-2 inline-block px-2 py-1 bg-blue-100 text-blue-500 text-xs rounded">
                        Active
                      </span>
            }
          </div>
        </div>
    )
  }, [ person ]);

  const showActionButtons = useCallback(() => {

    if (!person) return null;

    return person.status === 'DISABLED' ? null : (
        <div className="flex gap-3">
          <button
              onClick={() => setShowEditModal(true)}
              className="px-6 py-2 border border-gray-200 rounded-lg font-sans text-gray-600 hover:bg-gray-50 transition-colors">
            Edit
          </button>
          {
            session?.role !== Role.ADMIN ? null : (
                <button
                    onClick={handleDelete}
                    className="px-6 py-2 bg-blue-50 text-blue-500 rounded-lg font-sans hover:bg-blue-100 transition-colors">
                  Delete
                </button>
            )
          }
        </div>
    )
  }, [ person ]);

  const fetchPerson = async () => {
    setLoading(true);
    try {
      const data = await PersonCliService.getPerson(id);
      setPerson(data);
    } catch (error) {
      console.error("Error fetching person:", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPerson();
  }, [id]);

  const handleDelete = useCallback(async () => {
    if (window.confirm("Are you sure you want to delete this person?")) {
      try {
        await PersonCliService.deletePerson(id);
        router.push("/dashboard");
      } catch (error) {
        console.error("Error deleting person:", error);
        alert("Error deleting person");
      }
    }
  }, []);

  if (loading) {
    return (
      <AppLayout>
        <div className="flex items-center justify-center h-64">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-300"></div>
        </div>
      </AppLayout>
    );
  }

  if (!person) {
    return (
      <AppLayout>
        <div className="text-center py-12">
          <h2 className="text-2xl font-sans text-gray-800">Person not found</h2>
          <button
            onClick={() => router.push("/dashboard")}
            className="mt-4 text-blue-500 hover:underline"
          >
            Back to Dashboard
          </button>
        </div>
      </AppLayout>
    );
  }

  return (
    <AppLayout>
      <div className="max-w-3xl mx-auto">
        <button
          onClick={() => router.push("/dashboard")}
          className="mb-6 flex items-center text-medium-grey hover:text-gray-800 transition-colors"
        >
          <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
          </svg>
          Back
        </button>

        <div className="bg-white rounded-2xl shadow-sm overflow-hidden border border-gray-100">
          <div className="bg-light-blue h-32 flex items-end px-8 pb-0">
            <div className="w-24 h-24 bg-white rounded-2xl shadow-sm -mb-8 flex items-center justify-center text-blue-500 font-sans text-4xl border-4 border-white">
              <SVG name="person" className="w-16 h-16" />
            </div>
          </div>

          <div className="pt-12 px-8 pb-8">
            <div className="flex flex-col md:flex-row md:items-start justify-between gap-6">
              {showStatusBadge()}
              {showActionButtons()}
            </div>

            <div className="mt-12 space-y-8">
              <section>
                <h2 className="text-sm font-sans text-gray-400 uppercase tracking-wider mb-3">Biography</h2>
                <p className="text-gray-700 leading-relaxed whitespace-pre-wrap">
                  {person.bio || "No biography available for this person."}
                </p>
              </section>
            </div>
          </div>
        </div>
      </div>

      <Dialog open={showEditModal} onOpenChange={setShowEditModal}>
        {person && (
          <EditPersonModal
            person={person}
            onClose={() => setShowEditModal(false)}
            onUpdated={() => {
              fetchPerson().catch(doNothing);
              setShowEditModal(false);
            }}
          />
        )}
      </Dialog>
    </AppLayout>
  );
}

function EditPersonModal({ person, onClose, onUpdated }: { person: Person; onClose: () => void; onUpdated: () => void }) {
  const [formData, setFormData] = useState({
    id: person.id,
    name: person.name,
    phoneNumber: person.phoneNumber,
    bio: person.bio || ""
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    try {
      await PersonCliService.updatePerson(formData);
      onUpdated();
    } catch (err: any) {
      setError(err.message || "Error updating person");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="bg-white rounded-2xl w-full px-5">
      <div className="p-6 border-b">
        <h2 className="text-xl font-sans text-gray-800">Edit Person</h2>
      </div>
      <form onSubmit={handleSubmit} className="p-6 space-y-4">
        <div>
          <label className="block text-sm font-light text-gray-600 mb-1">Name</label>
          <input
            required
            className="w-full px-4 py-2 border rounded-lg outline-none focus:ring-2 focus:ring-blue-300"
            value={formData.name}
            onChange={(e) => setFormData({ ...formData, name: e.target.value })}
          />
        </div>
        <div>
          <label className="block text-sm font-light text-gray-600 mb-1">Phone</label>
          <input
            required
            className="w-full px-4 py-2 border rounded-lg outline-none focus:ring-2 focus:ring-blue-300"
            value={formData.phoneNumber}
            onChange={(e) => setFormData({ ...formData, phoneNumber: e.target.value })}
          />
        </div>
        <div>
          <label className="block text-sm font-light text-gray-600 mb-1">Bio</label>
          <textarea
            className="w-full px-4 py-2 border rounded-lg outline-none focus:ring-2 focus:ring-blue-300 h-24 resize-none"
            value={formData.bio}
            onChange={(e) => setFormData({ ...formData, bio: e.target.value })}
          />
        </div>

        {error && <p className="text-blue-500 text-sm">{error}</p>}

        <div className="flex gap-3 pt-4">
          <button
            type="button"
            onClick={onClose}
            className="flex-1 px-4 py-2 border rounded-lg font-sans text-gray-600 hover:bg-gray-50"
          >
            Cancel
          </button>
          <button
            type="submit"
            disabled={loading}
            className="flex-1 px-4 py-2 bg-blue-300 text-white rounded-lg font-sans hover:bg-blue-400 disabled:opacity-50"
          >
            {loading ? "Updating..." : "Update"}
          </button>
        </div>
      </form>
    </div>
  );
}
