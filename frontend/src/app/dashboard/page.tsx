"use client";

import React, { useState, useEffect, useCallback } from "react";
import { AppLayout } from "@/components/layout";
import { SVG } from "@/components";
import { PersonCliService } from "@/services/client/person.cli.service";
import { Person } from "@/types";
import Link from "next/link";
import { Dialog } from "@/components/ui/dialog";
import { useAppContext } from "@/providers/react-app";
import { doNothing, wait } from "@/lib";

export default function DashboardPage() {
  const { state: contextState } = useAppContext() as any;
  const initialPersons = contextState?.initialPersons as Person[] | undefined;

  const [persons, setPersons] = useState<Person[]>(initialPersons || []);
  const [loading, setLoading] = useState(!initialPersons);
  const [search, setSearch] = useState("");
  const [statusFilter, setStatusFilter] = useState({ enabled: true, disabled: false });
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(initialPersons ? initialPersons.length === 10 : true);
  const [showCreateModal, setShowCreateModal] = useState(false);

  const getStatusQuery = useCallback(() => {
    const active = [];
    if (statusFilter.enabled) active.push("1");
    if (statusFilter.disabled) active.push("0");


    if (active.length === 0 || active.length === 2) return "0,1";
    return active[0];
  }, [ statusFilter.enabled, statusFilter.disabled ]);

  const fetchPersons = useCallback(async (pageNum: number, isNewSearch = false) => {
    const currentStatus = getStatusQuery();

    if (isNewSearch && pageNum === 0 && search === "" && currentStatus === "1" && initialPersons) {
      setLoading(false);
      return;
    }

    setLoading(true);

    try {
      const data = search.trim()
          ? await PersonCliService.searchPersons(search, pageNum, 10, currentStatus)
          : await PersonCliService.getPersons(pageNum, 10, currentStatus);

      if (isNewSearch) {
        setPersons(data);
      } else {
        setPersons((prev) => [ ...prev, ...data ]);
      }
      setHasMore(data.length === 10);
    } catch (error) {
      console.error("Error fetching persons:", error);
    } finally {
      setLoading(false);
    }
  }, [ getStatusQuery, search, initialPersons ]);

  useEffect(() => {
    wait(500).then(() => {
      setPage(0);
      fetchPersons(0, true)
          .catch(doNothing)
    });
  }, [search, statusFilter]);

  useEffect(() => {

    if (!persons.length) fetchPersons(0, true).catch(doNothing);

  }, [persons]);

  const handleLoadMore = useCallback(() => {
    const nextPage = page + 1;
    setPage(nextPage);
    fetchPersons(nextPage);
  }, [ page ]);

  return (
    <AppLayout>
      <div className="space-y-6">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div className="flex-1 flex flex-col sm:flex-row items-start sm:items-center gap-4">
            <div className="relative flex-1 w-full">
              <input
                type="text"
                placeholder="Search people by name, phone number..."
                className="w-full pl-10 pr-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-300 outline-none"
                value={search}
                onChange={(e) => setSearch(e.target.value)}
              />
              <div className="absolute left-3 top-2.5 text-gray-400">
                <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                </svg>
              </div>
            </div>

            <div className="flex items-center gap-4 px-2">
              <label className="flex items-center gap-2 cursor-pointer group">
                <div className="relative">
                  <input
                    type="checkbox"
                    className="sr-only peer"
                    checked={statusFilter.enabled}
                    onChange={(e) => setStatusFilter({ ...statusFilter, enabled: e.target.checked })}
                  />
                  <div className="w-10 h-5 bg-gray-200 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full rtl:peer-checked:after:-translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:start-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-4 after:w-4 after:transition-all peer-checked:bg-blue-300"></div>
                </div>
                <span className="text-sm font-bold text-gray-600 group-hover:text-gray-900 transition-colors">Active</span>
              </label>

              <label className="flex items-center gap-2 cursor-pointer group">
                <div className="relative">
                  <input
                    type="checkbox"
                    className="sr-only peer"
                    checked={statusFilter.disabled}
                    onChange={(e) => setStatusFilter({ ...statusFilter, disabled: e.target.checked })}
                  />
                  <div className="w-10 h-5 bg-gray-200 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full rtl:peer-checked:after:-translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:start-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-4 after:w-4 after:transition-all peer-checked:bg-blue-300"></div>
                </div>
                <span className="text-sm font-bold text-gray-600 group-hover:text-gray-900 transition-colors">Inactive</span>
              </label>
            </div>
          </div>
          <button
            onClick={() => setShowCreateModal(true)}
            className="bg-blue-300 text-white px-6 py-2 rounded-lg font-sans hover:bg-blue-400 transition-colors">
            Add New Person
          </button>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
          {persons.map((person) => (
            <Link key={person.id} href={`/dashboard/person/${person.id}`}>
              <div className="bg-white p-6 rounded-xl shadow-sm hover:shadow-md transition-shadow cursor-pointer border border-transparent hover:border-blue-300 group">
                <div className={"row-span-2 flex"}>
                  <div className="w-16 h-16 bg-light-blue rounded-full mb-4 flex items-center justify-center text-blue-500 font-sans text-xl group-hover:bg-blue-300 transition-colors">
                    <SVG name="person" className="w-10 h-10" />
                  </div>
                  <span className={"ml-auto justify-self-start"}>
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
                  </span>
                </div>
                <h3 className="font-sans text-lg text-gray-800 mb-1">{person.name}</h3>
                <p className="text-medium-grey text-sm font-light">{person.phoneNumber}</p>
              </div>
            </Link>
          ))}
        </div>

        {loading && (
          <div className="flex justify-center py-8">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-300"></div>
          </div>
        )}

        {!loading && hasMore && (
          <div className="flex justify-center pt-4">
            <button
              onClick={handleLoadMore}
              className="text-blue-500 font-sans hover:underline"
            >
              Load more
            </button>
          </div>
        )}
      </div>

      <Dialog open={showCreateModal} onOpenChange={setShowCreateModal}>
        <CreatePersonModal
          onClose={() => setShowCreateModal(false)}
          onCreated={() => {
            setPage(0);
            fetchPersons(0, true);
            setShowCreateModal(false);
          }}
        />
      </Dialog>
    </AppLayout>
  );
}

function CreatePersonModal({ onClose, onCreated }: { onClose: () => void; onCreated: () => void }) {
  const [formData, setFormData] = useState({ name: "", phoneNumber: "", bio: "" });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const handleSubmit = async (e: React.SubmitEvent) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    try {
      await PersonCliService.createPerson(formData);
      onCreated();
    } catch (err: any) {
      setError(err.message || "Error creating person");
    } finally {
      setLoading(false);
    }
  };

  return (
      <div className="bg-white rounded-2xl w-full px-5">
      <div className="p-6 border-b">
        <h2 className="text-xl font-sans text-gray-800">New Person</h2>
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
            {loading ? "Creating..." : "Create"}
          </button>
        </div>
      </form>
    </div>
  );
}
